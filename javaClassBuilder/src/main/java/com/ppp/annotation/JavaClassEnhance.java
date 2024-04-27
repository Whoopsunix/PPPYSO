package com.ppp.annotation;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 * JavaClass 增强
 */
public enum JavaClassEnhance {
    Default,
    FreeMarker,
    ;

    public static JavaClassEnhance getJavaClassEnhanceEnums(String enchantEnums) {
        for (JavaClassEnhance value : values()) {
            if (value.name().equalsIgnoreCase(enchantEnums)) {
                return value;
            }
        }
        if (enchantEnums != null)
            Printer.warn(String.format("No such JavaClassEnhance: %s , use Default", enchantEnums));
        return Default;
    }
}
