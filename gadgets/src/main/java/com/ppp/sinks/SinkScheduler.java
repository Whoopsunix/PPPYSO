package com.ppp.sinks;

import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.maker.AnnotationUtils;
import com.ppp.utils.maker.ClassUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Whoopsunix
 */
public class SinkScheduler {
    private static String packageName = "com.ppp.sinks";

    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        // 通过
        sinksHelper.setSink(Sink.InvokerTransformer3);
        sinksHelper.setEnchant(EnchantType.RUNTIME);

        sinksHelper.setCommand("calc");

        SinkScheduler.builder(sinksHelper);
    }

    /**
     * 调用类支持的 Sink 增强功能
     */
    public static Object builder(SinksHelper sinksHelper) throws Exception {
        Class targetClass = null;
        Method targetMethod = null;

        String sink = sinksHelper.getSink();
        List<Class<?>> classes = ClassUtils.getClasses(packageName);
        for (Class<?> clazz : classes) {
            Sink classAnnotation = clazz.getAnnotation(Sink.class);
            if (classAnnotation != null && AnnotationUtils.containsValue(classAnnotation.value(), sink)) {
//                System.out.println("Class: " + clazz.getName() + ", Annotation Value: " + classAnnotation.value());
                targetClass = clazz;
                break;
            }
        }

        if (targetClass == null)
            return null;
        Method[] methods = targetClass.getDeclaredMethods();

        for (Method method : methods) {
            EnchantType enchantType = method.getAnnotation(EnchantType.class);
            if (enchantType != null && AnnotationUtils.containsValue(enchantType.value(), sinksHelper.getEnchant())) {
//                return method.invoke(sink.newInstance(), sinksHelper);
                targetMethod = method;
                break;
            }
        }

        if (targetMethod == null)
            Printer.error(String.format("%s no such enchant function: %s", sink, sinksHelper.getEnchant()));

        return targetMethod.invoke(targetClass.newInstance(), sinksHelper);
    }

}
