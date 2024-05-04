package com.ppp.annotation;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 * JavaClass 创建增强
 */
public enum JavaClassMakerEnhance {
    /**
     * 输出
     */
    Default("default"),
    JDK17("JDK17")
    ;
    private final String info;

    JavaClassMakerEnhance(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public static JavaClassMakerEnhance[] splitJavaClassMakerEnhance(String enchant) {
        String[] split = enchant.split(",");
        JavaClassMakerEnhance[] javaClassEnhances = new JavaClassMakerEnhance[split.length];
        for (int i = 0; i < split.length; i++) {
            javaClassEnhances[i] = getJavaClassMakerEnhanceEnums(split[i]);
        }
        return javaClassEnhances;
    }

    public static JavaClassMakerEnhance getJavaClassMakerEnhanceEnums(String enchantEnums) {
        for (JavaClassMakerEnhance value : values()) {
            if (value.name().equalsIgnoreCase(enchantEnums)) {
                return value;
            }
        }
        if (enchantEnums != null)
            Printer.warn(String.format("No such JavaClassMakerEnhance: %s , use Default", enchantEnums));
        return Default;
    }
}
