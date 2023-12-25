package com.ppp.sinks;

/**
 * @author Whoopsunix
 *
 * Sink 点增强配置项
 */
public class SinksHelper {
    /**
     * 增强 Sink 点类型, 对应 @Sink 注解
     */
    private String sink;
    /**
     * 增强功能，对应 @EnchantType 注解
     */
    private String enchant;

    /**
     * 以下为增强功能的配置参数
     */
    /**
     * 命令执行内容
     */
    private String command;

    public String getSink() {
        return sink;
    }

    public void setSink(String sink) {
        this.sink = sink;
    }

    public String getEnchant() {
        return enchant;
    }

    public void setEnchant(String enchant) {
        this.enchant = enchant;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
