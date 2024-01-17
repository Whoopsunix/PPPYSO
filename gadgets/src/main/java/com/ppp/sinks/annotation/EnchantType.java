package com.ppp.sinks.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * 功能增强类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnchantType {
    /**
     * 功能清单
     */
    // 默认功能
    String DEFAULT = "Default";
    // 命令执行
    String RUNTIME = "Runtime";
    // 命令执行 ProcessBuilder
    String ProcessBuilder = "ProcessBuilder";
    // 命令执行 ScriptEngine
    String ScriptEngine = "ScriptEngine";
    // 线程延时
    String Delay = "Delay";
    // Socket 探测
    String Socket = "Socket";
    // 文件写入
    String FileWrite = "FileWrite";
    // 远程类加载
    String RemoteLoad = "RemoteLoad";
    // 本地类加载
    String LocalLoad = "LocalLoad";
    // JavaClass
    String JavaClass = "JavaClass";

    String[] value() default {};

    public static class Utils {
        public static String[] getAuthors(AnnotatedElement annotated) {
            EnchantType enchantType = annotated.getAnnotation(EnchantType.class);
            if (enchantType != null && enchantType.value() != null) {
                return enchantType.value();
            } else {
                return new String[0];
            }
        }
    }
}
