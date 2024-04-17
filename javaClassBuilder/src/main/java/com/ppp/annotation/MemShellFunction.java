package com.ppp.annotation;

import com.ppp.Printer;

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
        public static String getMemShellFunction(String msf) {
            if (msf != null && msf.equalsIgnoreCase(MemShellFunction.Exec)) {
                return MemShellFunction.Exec;
            } else if (msf != null && msf.equalsIgnoreCase(MemShellFunction.Godzilla)) {
                return MemShellFunction.Godzilla;
            } else if (msf != null && msf.equalsIgnoreCase(MemShellFunction.Behinder)) {
                return MemShellFunction.Behinder;
            } else if (msf != null && msf.equalsIgnoreCase(MemShellFunction.sou5)) {
                return MemShellFunction.sou5;
            } else {
                Printer.error(String.format("MemShellFunction not found: %s", msf));
                return null;
            }
        }
    }
}
