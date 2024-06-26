package com.ppp.scheduler;

import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.annotation.Builder;
import com.ppp.annotation.JavaClassHelperType;
import com.ppp.annotation.JavaClassType;
import com.ppp.annotation.Middleware;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.ClassUtils;
import com.ppp.utils.maker.CryptoUtils;

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
        String javaClassType = javaClassHelper.getJavaClassType();

        /**
         * 获取 Builder
         */
        Class rceEchoClass = null;

        List<Class<?>> builderClasses = ClassUtils.getClasses(builderPackageName);
        for (Class<?> clazz : builderClasses) {
            Builder builder = clazz.getAnnotation(Builder.class);
            if (builder == null) continue;

//            Middleware middlewareAnnotation = clazz.getAnnotation(Middleware.class);
//            if (middlewareAnnotation == null) continue;

            // 获取 RceEcho Builder
            if (builder.value().equalsIgnoreCase(Builder.RceEcho)) {
                rceEchoClass = clazz;
//                Printer.log("RceEcho builder Class: " + clazz.getName() + ", Annotation Value: " + builder.value());
                break;
            }
        }


        /**
         * 获取 rceEcho 类
         */
        if (rceEchoClass == null) {
            Printer.error(String.format("The %s RceEcho is not supported", middleware));
        }

        Class recEchoJavaClass = null;
        List<Class<?>> rceEchoClasses = ClassUtils.getClasses(rceEchoPackageName);
        for (Class<?> clazz : rceEchoClasses) {
            Middleware middlewareAnnotation = clazz.getAnnotation(Middleware.class);
            if (middlewareAnnotation == null) continue;

            JavaClassType javaClassTypeAnnotation = clazz.getAnnotation(JavaClassType.class);
            if (javaClassTypeAnnotation == null) continue;

            if (middlewareAnnotation.value().equalsIgnoreCase(middleware) && javaClassTypeAnnotation.value().equalsIgnoreCase(javaClassType)) {
                recEchoJavaClass = clazz;
                Printer.log("RceEcho Class: " + clazz.getName() + " , Annotation Value: " + middlewareAnnotation.value() + " , JavaClassType: " + javaClassTypeAnnotation.value());
                break;
            }
        }

        byte[] recEchoJavaClassBytes = (byte[]) Reflections.invokeMethod(rceEchoClass.newInstance(), "build", new Class[]{Class.class, JavaClassHelper.class}, new Object[]{recEchoJavaClass, javaClassHelper});
        String b64 = CryptoUtils.base64encoder(recEchoJavaClassBytes);
//        Printer.yellowInfo("Rce echo:");
//        Printer.yellowInfo(b64);


        return recEchoJavaClassBytes;
    }
}
