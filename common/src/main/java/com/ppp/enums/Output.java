package com.ppp.enums;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 *
 * 加密方法
 */
public enum Output {
    Default,
    Base64,
    GZIP,
    ;

    public static Output getOutput(String output) {
        for (Output value : values()) {
            if (value.name().equalsIgnoreCase(output)) {
                return value;
            }
        }
        Printer.warn(String.format("No such output: %s , use Default", output));
        return Default;
    }
}