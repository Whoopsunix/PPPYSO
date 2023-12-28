package com.ppp.sinks;

import com.ppp.MemShellHelper;

/**
 * @author Whoopsunix
 * <p>
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
     * 输出
     */
    private String output;
    /**
     * 是否保存为文件
     */
    private boolean save = false;
    /**
     * 文件路径
     */
    private String savePath = "./result.bin";


    /**
     * 以下为增强功能的配置参数
     */
    /**
     * 命令执行内容
     */
    private String command;
    /**
     * 操作系统
     */
    private String os;
    /**
     * Socket 探测域名
     */
    private String host;
    /**
     * 内存马信息
     */
    private MemShellHelper memShellHelper;

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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public MemShellHelper getMemShellHelper() {
        return memShellHelper;
    }

    public void setMemShellHelper(MemShellHelper memShellHelper) {
        this.memShellHelper = memShellHelper;
    }
}
