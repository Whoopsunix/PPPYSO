package com.ppp.annotation;

import com.ppp.Printer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 内存马类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MemShell {
    String Listener = "Listener";
    String Servlet = "Servlet";
    String Filter = "Filter";
    String Executor = "Executor";
    String Controller = "Controller";
    String Valve = "Valve";
    String Interceptor = "Interceptor";

    String value();
//    String[] value() default {};

    public static class Utils {
        public static List show() {
            ArrayList result = new ArrayList();
            Field[] declaredFields = MemShell.class.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                try {
                    result.add(declaredField.get(null));
                } catch (IllegalAccessException e) {

                }
            }
            return result;
        }

        public static String getMemShell(String ms) {
            if (ms != null && ms.equalsIgnoreCase(MemShell.Listener)) {
                return MemShell.Listener;
            } else if (ms != null && ms.equalsIgnoreCase(MemShell.Servlet)) {
                return MemShell.Servlet;
            } else if (ms != null && ms.equalsIgnoreCase(MemShell.Filter)) {
                return MemShell.Filter;
            } else if (ms != null && ms.equalsIgnoreCase(MemShell.Controller)) {
                return MemShell.Controller;
            } else if (ms != null && ms.equalsIgnoreCase(MemShell.Valve)) {
                return MemShell.Valve;
            } else if (ms != null && ms.equalsIgnoreCase(MemShell.Executor)) {
                return MemShell.Executor;
            } else if (ms != null && ms.equalsIgnoreCase(MemShell.Interceptor)) {
                return MemShell.Interceptor;
            } else {
                Printer.warn(String.format("MemShell not found: %s", ms));
                return null;
            }
        }
    }


}
