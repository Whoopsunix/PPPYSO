package com.ppp.sinks.annotation;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 * <p>
 * 可选固定值参数
 */
public enum EnchantEnums {
    Default,
    // 操作系统
    WIN,
    // 本地字节码加载
    RHINO,
    // 延时
    Timeunit,
    // 二次反序列化
    SignedObject,
    RMIConnector,

    /**
     * 命令执行
     */
    Runtime,
    ProcessBuilder,
    ScriptEngine,
    SnakeYAML,
    ;

    public static EnchantEnums getEnchantEnums(String enchantEnums) {
        for (EnchantEnums value : values()) {
            if (value.name().equalsIgnoreCase(enchantEnums)) {
                return value;
            }
        }
        Printer.warn(String.format("No such enchantEnums: %s , use Default", enchantEnums));
        return Default;
    }
}
