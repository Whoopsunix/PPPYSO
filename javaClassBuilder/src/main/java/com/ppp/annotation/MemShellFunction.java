package com.ppp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 内存马功能
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MemShellFunction {
    String Exec = "Exec";
    String Godzilla = "Godzilla";
    String Behinder = "Behinder";
    String sou5 = "sou5";

    String value();

    public static class Utils {
        public static String getTargetMemShellFunction(String msf) {
            if (msf.equalsIgnoreCase(MemShellFunction.Exec)) {
                return MemShellFunction.Exec;
            } else if (msf.equalsIgnoreCase(MemShellFunction.Godzilla)) {
                return MemShellFunction.Godzilla;
            } else {
                return null;
            }
        }
    }
}
