package com.ppp;

import com.ppp.annotation.Builder;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import com.ppp.annotation.Middleware;
import com.ppp.utils.maker.ClassUtils;

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
        build(memShellHelper);
    }

    public static String build(MemShellHelper memShellHelper) throws Exception {
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

            // 获取 Loader
            if (builder.value().equals(Builder.Loader) && middlewareAnnotation.value().equals(middleware)) {
                loaderBuilderClass = clazz;
                System.out.println("Loader builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
            }
            // 获取内存马
            if (builder.value().equals(Builder.MS) && middlewareAnnotation.value().equals(middleware)) {
                msBuilderClass = clazz;
                System.out.println("MemShell builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
            }
        }

        // 组件类型不存在
        if (loaderBuilderClass == null) {
            System.out.println("Loader builder Class Not Found");
            return null;
        }
        if (msBuilderClass == null) {
            System.out.println("MemShell builder Class Not Found");
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
                System.out.println("MS builder Method: " + method.getName() + ", Annotation Value: " + memShellAnnotation.value());
                break;
            }
        }

        Method[] loaderMethods = loaderBuilderClass.getDeclaredMethods();
        for (Method method : loaderMethods) {
            MemShell memShellAnnotation = method.getAnnotation(MemShell.class);
            if (memShellAnnotation == null) continue;

            if (memShellAnnotation.value().equals(memShell)) {
                loaderMethod = method;
                System.out.println("Loader builder Method: " + method.getName() + ", Annotation Value: " + memShellAnnotation.value());
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
                System.out.println("Loader Class: " + clazz.getName() + ", Annotation Value: " + middlewareAnnotation.value());
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
                System.out.println("MemShell Class: " + clazz.getName() + ", Annotation Value: " + memShellAnnotation.value());
                break;
            }
        }

        if (loaderClass == null) {
            System.out.println("Loader Class Not Found");
            return null;
        }
        if (msClass == null) {
            System.out.println("MemShell Class Not Found");
            return null;
        }


        /**
         * 生成 Loader 和 MemShell
         */
        Object loaderBuilder = loaderBuilderClass.newInstance();
        Object msBuilder = msBuilderClass.newInstance();

        String msJavaClass = (String) msMethod.invoke(msBuilder, msClass);
        System.out.println("msJavaClass:");
        System.out.println(msJavaClass);

        String msLoaderJavaClass = (String) loaderMethod.invoke(loaderBuilder, loaderClass, msJavaClass);
        System.out.println("ms:");
        System.out.println(msLoaderJavaClass);

        return msLoaderJavaClass;

    }

}
