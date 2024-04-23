package com.ppp.annotation;

import com.ppp.Printer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 生成类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaClassType {
    String Default = "Default";
    String AutoFind = "AutoFind";

    String value();

    public static class Utils {
        public static String getJavaClassType(String javaClassType) {
            if (javaClassType != null && javaClassType.equalsIgnoreCase(JavaClassType.AutoFind)) {
                return JavaClassType.AutoFind;
            } else {
                Printer.blueInfo("JavaClassType not found use Default");
                return JavaClassType.Default;
            }
        }
    }
}
