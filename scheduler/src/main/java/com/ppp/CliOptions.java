package com.ppp;

/**
 * @author Whoopsunix
 */
public enum CliOptions {
    Help("h", "help", "Print this usage information"),
    // 基本参数
    Gadget("g", "gadget", "Gadget class name"),
    Show("show", "show", "Show gadget enhances"),
    SerializationType("st", "serializationType", "eg. [Default | XStream | HexAscii | UTF8Mix]"),
    Output("o", "output", "Gadget output type"),
    SavePath("save", "savePath", "Save payload to file"),
    ClosePrinter("close", "closePrinter", "Turn off all runtime output and keep only the results"),

    // 增强类型
    Enchant("e", "enchant", "Enchant type"),

    // 命令执行
    Command("cmd", "command", "Command"),
    CommandType("cmdt", "commandType", "Command Type"),
    Split("split", "commandSplitsplit", ""),
    // 代码执行
    Code("code", "code", "Code execution content"),
    CodeFile("cf", "codeFile", "Read code execution content from local file"),
    // 线程延时类型
//    Delay("d", "delay", "Delay function, default is Thread.sleep"),
    // 线程延时时间
    DelayTime("dt", "delayTime", "Delay time (in seconds)"),
    Host("host", "host", "Host"),
    // 服务器文件路径
    ServerFilePath("sfp", "serverFilePath", "Server file path"),
    // 本地文件路径
    LocalFilePath("lfp", "localFilePath", "Local file path"),
    // 文件内容
    FileContent("fc", "fileContent", "File content"),
    Append("append", "append", "bytes will be written to the end of the file"),
    PartSize("part", "partSize", "Split file size, default is 100KB"),

    // 远程加载 url
    URL("u", "url", "URL"),
    // 类名
    ClassName("cn", "className", "Class Name"),
    // 远程加载构造方法参数
    Constructor("ctor", "constructor", "Constructor param"),
    // 本地加载方法
    LoadFunction("lf", "loadFunction", "Load function [Default | RHINO]"),

    /**
     * JavaClass
     */
    // 是否继承 AbstractTranslet
    ExtendsAbstractTranslet("ext", "extendsAbstractTranslet", "Extends AbstractTranslet"),
    JavaClassHelperType("jht", "javaClassHelperType", "eg. [MemShell | RceEcho | Custom]"),
    JavaClassFilePath("jfp", "javaClassFilePath", "Local JavaClass from local file path"),
    JavaClassEnhance("je", "javaClassEnhance", "eg. [FreeMarker]"),
    JavaClassType("jt", "javaClassType", "[AutoFind | Default]"),
    Middleware("mw", "middleware", "eg. [Tomcat | Jetty ...]"),
    MemShell("ms", "memShell", "eg. [Servlet | Listener | Filter ...]"),
    MemShellFunction("msf", "memShellFunction", "eg. [Exec | Godzilla ...]"),

    // JavaClass Field 修改
    FieldNAME("fname", "fieldName", "Modify the field NAME"),
    FieldHEADER("fheader", "filedHeader", "Modify the field HEADER"),
    //    FieldPARAM("fparam", "filedParam", "Modify the field PARAM"),
    FieldPATH("fpath", "filedPath", "Modify the field PATH"),
    Fieldkey("fkey", "filedKey", "Modify the field key"),
    Fieldpass("fpass", "filedPass", "Modify the field pass"),
    FieldLockHeaderKey("flk", "filedLockHeaderKey", "Modify the field lockHeaderKey"),
    FieldLockHeaderValue("flv", "filedLockHeaderValue", "Modify the field lockHeaderValue"),

    // 二次反序列化
    WrapSerialization("wrap", "WrapSerialization", "Wrap Serialization"),
    CBVersion("cb", "cbVersion", "eg. [1.8.3 | 1.6 | 1.5]"),
    GadgetDependency("gd", "gadgetDependency", "eg. [rome | rometools]"),

    // 随机类名的包名
//    JavaClassPackageHost("jph", "javaClassPackageHost", "Java Class Package Host"),

    /**
     * DNS
     */
    DNSProducts("dp", "dnsProducts", "choose the products detected by dns"),
    DNSSubdomain("ds", "dnsSubdomain", "the dns subdomain bound with the class name"),
    ;


    private final String opt;
    private final String longOpt;
    private final String description;

    CliOptions(String opt, String longOpt, String description) {
        this.opt = opt;
        this.longOpt = longOpt;
        this.description = description;
    }

    public String getOpt() {
        return opt;
    }

    public String getLongOpt() {
        return longOpt;
    }

    public String getDescription() {
        return description;
    }
}
