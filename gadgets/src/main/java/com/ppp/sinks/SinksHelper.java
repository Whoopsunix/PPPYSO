package com.ppp.sinks;

import com.ppp.JavaClassHelper;
import com.ppp.chain.commonsbeanutils.CBVersionEnum;
import com.ppp.chain.urldns.DNSHelper;
import com.ppp.enums.Output;
import com.ppp.enums.SerializationType;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;

import java.util.List;

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
     * 代表本次为循环生成
     */
    private boolean loop;
    /**
     * 输出类型
     */
    private Output[] output;
    /**
     * 序列化类型
     */
    private SerializationType serializationType;
    /**
     * 是否保存为文件
     */
    private boolean save;
    /**
     * payload 保存文件路径
     */
    private String savePath;
    /**
     * 二次反序列化
     */
    private EnchantEnums wrapSerialization;
    /**
     * CB 版本
     */
    private CBVersionEnum cbVersionEnum;

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
    private EnchantEnums commandType;
    /**
     * 是否分割  支持分割命令、分割文件
     */
    private boolean split;
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
     * 追加写入文件
     */
    private boolean append;
    /**
     * 文件分片数组
     */
    private List<byte[]> fileParts;
    /**
     * 直接传入数组
     */
    private byte[] fileBytes;
    /**
     * 分片大小 kb 为单位
     */
    private Integer partSize;
    /**
     * 远程加载 url
     */
    private String url;
    /**
     * 类名
     */
    private String className;
    /**
     * 远程加载构造方法参数
     */
    private Object constructor;
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
        this.loop = false;
        this.enchant = EnchantType.DEFAULT;
        this.serializationType = SerializationType.Default;
        this.javaClassHelper = new JavaClassHelper();
        this.dnsHelper = new DNSHelper();
        this.output = new Output[]{Output.Default};
        this.save = true;
        this.savePath = "./result.bin";
        this.cbVersionEnum = CBVersionEnum.Default;
        this.commandType = EnchantEnums.Default;
        this.split = false;
        this.append = false;
        this.partSize = 100;
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

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public Output[] getOutput() {
        return output;
    }

    public void setOutput(Output[] output) {
        this.output = output;
    }

    public SerializationType getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(SerializationType serializationType) {
        this.serializationType = serializationType;
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

    public CBVersionEnum getCbVersion() {
        return cbVersionEnum;
    }

    public void setCbVersion(CBVersionEnum cbVersionEnum) {
        this.cbVersionEnum = cbVersionEnum;
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

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
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

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public List<byte[]> getFileParts() {
        return fileParts;
    }

    public void setFileParts(List<byte[]> fileParts) {
        this.fileParts = fileParts;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public Integer getPartSize() {
        return partSize;
    }

    public void setPartSize(Integer partSize) {
        this.partSize = partSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object getConstructor() {
        return constructor;
    }

    public void setConstructor(Object constructor) {
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
}
