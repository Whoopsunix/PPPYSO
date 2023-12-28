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
     * payload 保存文件路径
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
     * 服务器文件路径
     */
    private String serverFilePath;
    /**
     * 本地文件路径
     */
    private String localFilePath;
    /**
     * 文件内容
     */
    private String fileContent;


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

    public String getServerFilePath() {
        return serverFilePath;
    }

    public void setServerFilePath(String serverFilePath) {
        this.serverFilePath = serverFilePath;
    }

    public MemShellHelper getMemShellHelper() {
        return memShellHelper;
    }

    public void setMemShellHelper(MemShellHelper memShellHelper) {
        this.memShellHelper = memShellHelper;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }
}
