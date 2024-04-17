package com.ppp;

/**
 * @author Whoopsunix
 */
public enum CliOptions {
    Help("h", "help", "Print this usage information"),
    // 基本参数
    Gadget("g", "gadget", "Gadget class name"),
    SerializationType("st", "serializationType", "Serialization type"),
    Output("o", "output", "Gadget output type"),
    SavePath("save", "savePath", "Save payload to file"),
    ClosePrinter("close", "closePrinter", "Turn off all runtime output and keep only the results"),

    // 增强类型
    Enchant("e", "enchant", "Enchant type"),

    // 命令执行
    Command("cmd", "command", "Command"),
    // 操作系统
    OS("os", "os", "OS"),
    // 代码执行
    Code("code", "code", "Code execution content"),
    CodeFile("cf", "codeFile", "Read code execution content from local file"),
    // 线程延时类型
//    Delay("d", "delay", "Delay function, default is Thread.sleep"),
    // 线程延时时间
    DelayTime("dt", "delayTime", "Delay time (in seconds)"),
    // Socket 探测域名
    Host("host", "host", "Host"),
    // 服务器文件路径
    ServerFilePath("sfp", "serverFilePath", "Server file path"),
    // 本地文件路径
    LocalFilePath("lfp", "localFilePath", "Local file path"),
    // 文件内容
    FileContent("fc", "fileContent", "File content"),
    // 远程加载 url
    URL("u", "url", "URL"),
    // 远程加载类名
    RemoteClassName("rcn", "remoteClassName", "Remote Load Class Name"),
    // 远程加载构造方法参数
    Constructor("ctor", "constructor", "Constructor param"),
    // 本地加载方法
    LoadFunction("lf", "loadFunction", "Load function"),

    /**
     * JavaClass
     */
    // 是否继承 AbstractTranslet
    ExtendsAbstractTranslet("ext", "extendsAbstractTranslet", "Extends AbstractTranslet"),
    JavaClassHelperType("jht", "javaClassHelperType", "eg. [MemShell | RceEcho | Custom]"),
    JavaClassFilePath("jfp", "javaClassFilePath", "Local JavaClass from local file path"),
    JavaClassType("jt", "javaClassType", "[AutoFind | Default]"),
    Middleware("mw", "middleware", "eg. [Tomcat | Jetty ...]"),
    MemShell("ms", "memShell", "eg. [Servlet | Listener | Filter ...]"),
    MemShellFunction("msf", "memShellFunction", "eg. [Exec | Godzilla]"),

    // JavaClass Field 修改
    FieldNAME("fname", "fieldName", "Modify the field NAME"),
    FieldHEADER("fheader", "filedHeader", "Modify the field HEADER"),
    FieldPARAM("fparam", "filedParam", "Modify the field PARAM"),
    FieldPATH("fpath", "filedPath", "Modify the field PATH"),
    Fieldkey("fkey", "filedKey", "Modify the field key"),
    Fieldpass("fpass", "filedPass", "Modify the field pass"),
    FieldLockHeaderKey("flk", "filedLockHeaderKey", "Modify the field lockHeaderKey"),
    FieldLockHeaderValue("flv", "filedLockHeaderValue", "Modify the field lockHeaderValue"),

    // 二次反序列化
    WrapSerialization("wrap", "WrapSerialization", "Wrap Serialization"),
    CBVersion("cb", "cbVersion", "CB Version [1.8.3]"),

    // 随机类名的包名
//    JavaClassPackageHost("jph", "javaClassPackageHost", "Java Class Package Host"),

    /**
     * DNS
     */
    DNSHost("dh", "dnsHost", "dns log host"),
    DNSProducts("dp", "dnsProducts", "choose the products detected by dns"),
    DNSClassName("dcn", "dnsClassName", "the full class name detected by dns"),
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
