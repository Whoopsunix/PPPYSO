package com.ppp.sinks;

import com.ppp.JavaClassHelper;
import com.ppp.chain.urldns.DNSHelper;
import com.ppp.enums.Output;
import com.ppp.enums.SerializationType;
import com.ppp.sinks.annotation.EnchantEnums;
import jnr.ffi.annotations.Out;

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
     * 输出类型
     */
    private Output output = Output.Default;
    /**
     * 序列化类型
     */
    private SerializationType serialization = SerializationType.Default;
    /**
     * 是否保存为文件
     */
    private boolean save = false;
    /**
     * payload 保存文件路径
     */
    private String savePath = "./result.bin";
    /**
     * 二次反序列化
     */
    private EnchantEnums wrapSerialization;
    private String CBVersion = "dafault";

    /**
     * 以下为增强功能的配置参数
     */
    /**
     * 命令执行内容
     */
    private String command;
    /**
     * 命令执行类型
     */
    private EnchantEnums commandType = EnchantEnums.Default;
    /**
     * 操作系统
     */
    private EnchantEnums os;
    /**
     * 代码执行内容
     */
    private String code;
    /**
     * 代码执行内容 文件中读取
     */
    private String codeFile;
    /**
     * Socket 探测域名
     */
    private String host;
    /**
     * 线程延时类型
     */
    private EnchantEnums delay;
    /**
     * 线程延时时间
     */
    private Long delayTime;
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
    private EnchantEnums loadFunction;
    /**
     * JavaClass 信息
     */
    private JavaClassHelper javaClassHelper;

    /**
     * URLDNS
     */
    private DNSHelper dnsHelper;

    public SinksHelper() {
        this.javaClassHelper = new JavaClassHelper();
    }

    /**
     * Field
     */
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

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public SerializationType getSerialization() {
        return serialization;
    }

    public void setSerialization(SerializationType serialization) {
        this.serialization = serialization;
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

    public EnchantEnums getWrapSerialization() {
        return wrapSerialization;
    }

    public void setWrapSerialization(EnchantEnums wrapSerialization) {
        this.wrapSerialization = wrapSerialization;
    }

    public String getCBVersion() {
        return CBVersion;
    }

    public void setCBVersion(String CBVersion) {
        this.CBVersion = CBVersion;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public EnchantEnums getCommandType() {
        return commandType;
    }

    public void setCommandType(EnchantEnums commandType) {
        this.commandType = commandType;
    }

    public EnchantEnums getOs() {
        return os;
    }

    public void setOs(EnchantEnums os) {
        this.os = os;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeFile() {
        return codeFile;
    }

    public void setCodeFile(String codeFile) {
        this.codeFile = codeFile;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public EnchantEnums getDelay() {
        return delay;
    }

    public void setDelay(EnchantEnums delay) {
        this.delay = delay;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
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

    public EnchantEnums getLoadFunction() {
        return loadFunction;
    }

    public void setLoadFunction(EnchantEnums loadFunction) {
        this.loadFunction = loadFunction;
    }

    public JavaClassHelper getJavaClassHelper() {
        return javaClassHelper;
    }

    public void setJavaClassHelper(JavaClassHelper javaClassHelper) {
        this.javaClassHelper = javaClassHelper;
    }

    public DNSHelper getDnsHelper() {
        return dnsHelper;
    }

    public void setDnsHelper(DNSHelper dnsHelper) {
        this.dnsHelper = dnsHelper;
    }

    @Override
    public String toString() {
        return "SinksHelper{" +
                "sink='" + sink + '\'' +
                ", enchant='" + enchant + '\'' +
                ", output=" + output +
                ", serialization=" + serialization +
                ", save=" + save +
                ", savePath='" + savePath + '\'' +
                ", wrapSerialization=" + wrapSerialization +
                ", CBVersion='" + CBVersion + '\'' +
                ", command='" + command + '\'' +
                ", commandType=" + commandType +
                ", os=" + os +
                ", code='" + code + '\'' +
                ", codeFile='" + codeFile + '\'' +
                ", host='" + host + '\'' +
                ", delay=" + delay +
                ", delayTime=" + delayTime +
                ", serverFilePath='" + serverFilePath + '\'' +
                ", localFilePath='" + localFilePath + '\'' +
                ", fileContent='" + fileContent + '\'' +
                ", url='" + url + '\'' +
                ", remoteClassName='" + remoteClassName + '\'' +
                ", constructor='" + constructor + '\'' +
                ", loadFunction=" + loadFunction +
                ", javaClassHelper=" + javaClassHelper +
                ", dnsHelper=" + dnsHelper +
                '}';
    }
}
