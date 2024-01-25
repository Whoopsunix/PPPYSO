package com.ppp.utils.maker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Whoopsunix
 */
public class ClassUtils {

    /**
     * 获取指定包下的所有类
     */
    public static List<Class<?>> getClasses(String packageName) throws Exception {
        List<Class<?>> classes = getJarClasses(packageName);
        if (classes.isEmpty())
            classes = getSourceClasses(packageName);

        return classes;
    }

    /**
     * 获取指定 Jar 包下的所有类
     */
    public static List<Class<?>> getJarClasses(String packageName) throws Exception {
        String path = packageName.replace('.', File.separatorChar);
        List<Class<?>> classes = listClassesInPackage(path);
        return classes;
    }

    public static List<Class<?>> listClassesInPackage(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList();

        String jarFilePath = getRunningJarFilePath();

        if (jarFilePath != null) {
            JarFile jarFile = new JarFile(jarFilePath);

            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().startsWith(packageName))
                    continue;

                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().substring(0, entry.getName().length() - 6).replace("/", ".");
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }

            jarFile.close();
        }
        return classes;
    }

    public static String getRunningJarFilePath() {
        String path = ClassUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = null;

        try {
            decodedPath = new File(path).getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return decodedPath.endsWith(".jar") ? decodedPath : null;
    }


    /**
     * 获取指定文件下的所有类 测试用
     */
    public static List<Class<?>> getSourceClasses(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', File.separatorChar);
        Enumeration<URL> resources = classLoader.getResources(path);

        List<Class<?>> classes = new ArrayList();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File directory = new File(resource.getFile());
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * 获取指定文件目录下的所有类
     */
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
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
//                Class<?> clazz = Class.forName(className);
//                classes.add(clazz);
                // 换成 ClassLoader.loadClass() 不会触发 static 代码块
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                classes.add(clazz);
            }
        }

        return classes;
    }
}
