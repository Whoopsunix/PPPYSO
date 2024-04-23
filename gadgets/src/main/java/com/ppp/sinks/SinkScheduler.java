package com.ppp.sinks;

import com.ppp.ObjectPayload;
import com.ppp.Printer;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.maker.AnnotationUtils;
import com.ppp.utils.maker.ClassUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Whoopsunix
 */
public class SinkScheduler {
    private static String sinksPackageName = "com.ppp.sinks";
    private static String gadgetPackageName = "com.ppp.chain";

    public static void main(String[] args) throws Exception {
        showGadget();
    }

    /**
     * 调用类支持的 Sink 增强功能
     */
    public static Object builder(SinksHelper sinksHelper) throws Exception {
        Class targetClass = null;
        Method targetMethod = null;

        String sink = sinksHelper.getSink();
        List<Class<?>> classes = ClassUtils.getClasses(sinksPackageName);
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

    /**
     * 展示调用链
     *
     * @throws Exception
     */
    public static void showGadget() throws Exception {
        List<Class<?>> classes = ClassUtils.getClasses(gadgetPackageName);
        System.out.println("Available payload types:\n");
        System.out.printf("%-25s\t%-25s\t%-25s\t%-25s\t%n", "Payload", "Sink", "Authors", "Dependencies");
        System.out.printf("%-25s\t%-25s\t%-25s\t%-25s\t%n", "--------", "--------", "--------", "--------");
        for (Class<?> clazz : classes) {
            Sink classAnnotation = clazz.getAnnotation(Sink.class);
            Authors authors = clazz.getAnnotation(Authors.class);
            Dependencies dependencies = clazz.getAnnotation(Dependencies.class);
            if (classAnnotation != null) {
                System.out.printf("%-25s\t%-25s\t%-25s\t%-25s%n", clazz.getSimpleName(), classAnnotation.value()[0], Arrays.toString(authors.value()), Arrays.toString(dependencies.value()));
            }
        }
    }

    /**
     * 获取调用链
     *
     * @param gadget
     * @return
     * @throws Exception
     */
    public static Class<? extends ObjectPayload> getGadgetClass(String gadget) throws Exception {
        // 调用链检查
        List<Class<?>> classes = ClassUtils.getClasses(gadgetPackageName);
        for (Class<?> clazz : classes) {
            String className = clazz.getSimpleName();
            if (className.equalsIgnoreCase(gadget)) {
                return (Class<? extends ObjectPayload>) clazz;
            }
        }
        Printer.error(String.format("No such gadget: %s", gadget));
        return null;
    }

    public static void showGadgetClassEnhances(Class<?> cls) throws Exception {
        Sink clsAnnotation = cls.getAnnotation(Sink.class);
        List<Class<?>> classes = ClassUtils.getClasses(sinksPackageName);
        List<String> enhances = new LinkedList<String>();
        for (Class<?> clazz : classes) {
            Sink classAnnotation = clazz.getAnnotation(Sink.class);
            if (classAnnotation != null && classAnnotation.equals(clsAnnotation)) {
                Printer.yellowInfo("Gadget: " + cls.getSimpleName());
                Printer.yellowInfo("Sink: " + classAnnotation.value()[0]);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    EnchantType enchantType = method.getAnnotation(EnchantType.class);
                    if (enchantType != null) {
                        enhances.add(enchantType.value()[0]);
                    }
                }
            }
        }
        Printer.yellowInfo("Available enchants: " + enhances);
        System.exit(0);


    }


}
