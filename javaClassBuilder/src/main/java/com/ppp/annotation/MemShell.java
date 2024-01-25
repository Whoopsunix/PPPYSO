package com.ppp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 内存马类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MemShell {
    String Listener = "Listener";
    String Servlet = "Servlet";
    String Executor = "Executor";

    String[] value() default {};

    public static class Utils {
        public static String getTargetMemShell(String ms) {
            if (ms.equalsIgnoreCase(MemShell.Listener)) {
                return MemShell.Listener;
            } else if (ms.equalsIgnoreCase(MemShell.Servlet)) {
                return MemShell.Servlet;
            } else if (ms.equalsIgnoreCase(MemShell.Executor)) {
                return MemShell.Executor;
            } else {
                return null;
            }
        }
    }


}
