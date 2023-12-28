package com.ppp.sinks.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

/**
 * 增强功能名
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnchantType {
    // 默认功能
    String DEFAULT = "Default";
    String RUNTIME = "Runtime";
    String MEMSHELL = "MemShell";

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
