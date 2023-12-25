package com.ppp.sinks;

import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Whoopsunix
 */
public class SinkScheduler {
    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(Sink.InvokerTransformer3);
        sinksHelper.setEnchant(EnchantType.RUNTIME);

        sinksHelper.setCommand("calc");

        SinkScheduler.make(sinksHelper);
    }

    /**
     * 调用类支持的 Sink 增强功能
     */
    public static Object make(SinksHelper sinksHelper) throws Exception {
        Class targetClass = null;
        Method targetMethod = null;

        String sink = sinksHelper.getSink();
        List<Class<?>> classes = getClasses("com.ppp.sinks");
        for (Class<?> clazz : classes) {
            Sink classAnnotation = clazz.getAnnotation(Sink.class);
            if (classAnnotation != null && containsValue(classAnnotation.value(), sink)) {
//                System.out.println("Class: " + clazz.getName() + ", Annotation Value: " + classAnnotation.value());
                targetClass = clazz;
                break;
            }
        }

        if (targetClass == null)
            return null;
        Method[] methods = targetClass.getDeclaredMethods();

        for (Method method : methods) {
            EnchantType enchantType = method.getAnnotation(EnchantType.class);
            if (enchantType != null && containsValue(enchantType.value(), sinksHelper.getEnchant())) {
//                return method.invoke(sink.newInstance(), sinksHelper);
                targetMethod = method;
                break;
            }
        }

        return targetMethod.invoke(targetClass.newInstance(), sinksHelper);
    }

    /**
     * 是否含有指定注解
     *
     * @param values
     * @param targetValue
     * @return
     */
    private static boolean containsValue(String[] values, String targetValue) {
        for (String value : values) {
            if (value.equals(targetValue)) {
                return true;
            }
        }
        return false;
    }

    // 获取指定包下的所有类
    private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<Class<?>> classes = new ArrayList();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }

        return classes;
    }
}
