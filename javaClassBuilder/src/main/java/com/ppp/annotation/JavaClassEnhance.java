package com.ppp.annotation;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 * JavaClass 输出增强
 */
public enum JavaClassEnhance {
    /**
     * 输出
     */
    Default("default"),
    Script("Script"),
    SPEL("SPEL"),
    SPELLoadClass("SPEL LoadClass"),
    FreeMarker("FreeMarker"),
    ;
    private final String info;

    JavaClassEnhance(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public static JavaClassEnhance[] splitJavaClassEnhance(String enchant) {
        String[] split = enchant.split(",");
        JavaClassEnhance[] javaClassEnhances = new JavaClassEnhance[split.length];
        for (int i = 0; i < split.length; i++) {
            javaClassEnhances[i] = getJavaClassEnhanceEnums(split[i]);
        }
        return javaClassEnhances;
    }

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
