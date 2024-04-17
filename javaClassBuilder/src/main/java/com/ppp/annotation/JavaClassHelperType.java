package com.ppp.annotation;

import com.ppp.Printer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JavaClass 类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaClassHelperType {
    String MemShell = "MemShell";
    String RceEcho = "RceEcho";
    // 自定义
    String Custom = "Custom";

    String value();

    public static class Utils {
        public static String getJavaClassHelperType(String javaClassHelperType) {
            if (javaClassHelperType != null && javaClassHelperType.equalsIgnoreCase(JavaClassHelperType.MemShell)) {
                return JavaClassHelperType.MemShell;
            } else if (javaClassHelperType != null && javaClassHelperType.equalsIgnoreCase(JavaClassHelperType.RceEcho)) {
                return JavaClassHelperType.RceEcho;
            } else if (javaClassHelperType != null && javaClassHelperType.equalsIgnoreCase(JavaClassHelperType.Custom)) {
                return JavaClassHelperType.Custom;
            } else {
                Printer.error(String.format("JavaClassHelperType not found: %s", javaClassHelperType));
                return null;
            }
        }
    }
}
