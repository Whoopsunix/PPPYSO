package com.ppp.scheduler;

import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.annotation.*;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.ClassUtils;
import com.ppp.utils.maker.Encoder;

import java.util.List;

/**
 * @author Whoopsunix
 */
@JavaClassHelperType(JavaClassHelperType.RceEcho)
public class RceEchoScheduler {
    private static String builderPackageName = "com.ppp.middleware.builder";
    private static String rceEchoPackageName = "com.ppp.middleware.rceecho";

    public static byte[] build(JavaClassHelper javaClassHelper) throws Exception {
        String middleware = javaClassHelper.getMiddleware();

        /**
         * 获取 Builder
         */
        Class recEchoClass = null;

        List<Class<?>> builderClasses = ClassUtils.getClasses(builderPackageName);
        for (Class<?> clazz : builderClasses) {
            Builder builder = clazz.getAnnotation(Builder.class);
            if (builder == null) continue;

            Middleware middlewareAnnotation = clazz.getAnnotation(Middleware.class);
            if (middlewareAnnotation == null) continue;

            // 获取 RceEcho Builder
            if (builder.value().equals(Builder.RceEcho) && middlewareAnnotation.value().equals(middleware)) {
                recEchoClass = clazz;
                Printer.blueInfo("RceEcho builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
            }
        }


        /**
         * 生成 Builder
         */
        if (recEchoClass == null) {
            Printer.error("RecEcho Class Not Found");
            return null;
        }

        Class recEchoJavaClass = null;
        List<Class<?>> rceEchoClasses = ClassUtils.getClasses(rceEchoPackageName);
        for (Class<?> clazz : rceEchoClasses) {
            Middleware middlewareAnnotation = clazz.getAnnotation(Middleware.class);
            if (middlewareAnnotation == null) continue;

            if (middlewareAnnotation.value().equals(middleware)) {
                recEchoJavaClass = clazz;
                Printer.blueInfo("RceEcho Class: " + clazz.getName() + ", Annotation Value: " + middlewareAnnotation.value());
            }
        }

        byte[] recEchoJavaClassBytes = (byte[]) Reflections.invokeMethod(recEchoClass.newInstance(), "build", new Class[]{Class.class, JavaClassHelper.class}, new Object[]{recEchoJavaClass, javaClassHelper});
        String b64 = Encoder.base64encoder(recEchoJavaClassBytes);
        Printer.greenInfo("rce echo:");
        Printer.greenInfo(b64);


        return recEchoJavaClassBytes;
    }
}
