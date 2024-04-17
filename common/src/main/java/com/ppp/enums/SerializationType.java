package com.ppp.enums;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 *
 * 序列化方法
 */
public enum SerializationType {
    Default,
    XStream,
    HexAscii,
    UTF8Mix,
    ;

    public static SerializationType getSerializationType(String serializationType) {
        for (SerializationType value : values()) {
            if (value.name().equalsIgnoreCase(serializationType)) {
                return value;
            }
        }
        Printer.warn(String.format("No such serializationType: %s , use Default", serializationType));
        return Default;
    }
}