package com.ppp.sinks;

import com.ppp.JavaClassHelper;

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

    private boolean extendsAbstractTranslet = false;

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
     * 线程延时类型
     */
    String sleep;
    /**
     * 线程延时时间
     */
    Long sleepTime;
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
     * 远程加载 url
     */
    private String url;
    /**
     * 远程加载类名
     */
    private String remoteClassName;
    /**
     * 远程加载构造方法参数
     */
    private String constructor;
    /**
     * 本地加载方法
     */
    private String loadFunction;
    /**
     * 内存马信息
     */
    private JavaClassHelper javaClassHelper;


    //
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

    public boolean isExtendsAbstractTranslet() {
        return extendsAbstractTranslet;
    }

    public void setExtendsAbstractTranslet(boolean extendsAbstractTranslet) {
        this.extendsAbstractTranslet = extendsAbstractTranslet;
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

    public String getSleep() {
        return sleep;
    }

    public void setSleep(String sleep) {
        this.sleep = sleep;
    }

    public Long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getServerFilePath() {
        return serverFilePath;
    }

    public void setServerFilePath(String serverFilePath) {
        this.serverFilePath = serverFilePath;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemoteClassName() {
        return remoteClassName;
    }

    public void setRemoteClassName(String remoteClassName) {
        this.remoteClassName = remoteClassName;
    }

    public String getConstructor() {
        return constructor;
    }

    public void setConstructor(String constructor) {
        this.constructor = constructor;
    }

    public String getLoadFunction() {
        return loadFunction;
    }

    public void setLoadFunction(String loadFunction) {
        this.loadFunction = loadFunction;
    }

    public JavaClassHelper getJavaClassHelper() {
        return javaClassHelper;
    }

    public void setJavaClassHelper(JavaClassHelper javaClassHelper) {
        this.javaClassHelper = javaClassHelper;
    }
}
