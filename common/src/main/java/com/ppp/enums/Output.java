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

    public static Output[] splitOutput(String output) {
        String[] split = output.split(",");
        Output[] outputs = new Output[split.length];
        for (int i = 0; i < split.length; i++) {
            outputs[i] = getOutput(split[i]);
        }
        return outputs;
    }
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