package com.ppp;

/**
 * @author Whoopsunix
 */
public enum CliOptions {
    Help("h", "help", "Print this usage information"),
    // 基本参数
    Gadget("g", "gadget", "Gadget class name"),
    Output("o", "output", "Gadget output type"),
    SavePath("save", "savePath", "Save payload to file"),
    // 增强类型
    Enchant("e", "enchant", "Enchant type"),
    // 命令执行内容
    Command("cmd", "command", "Command"),
    // 操作系统
    OS("os", "os", "OS"),
    Code("code", "code", "Code execution content"),
    CodeFile("cf", "codeFile", "Read Code execution content from file"),
    // Socket 探测域名
    Host("host", "host", "Host"),
    // 线程延时类型
    Delay("d", "delay", "Delay function, default is Thread.sleep"),
    // 线程延时时间
    DelayTime("dt", "delayTime", "Delay time (in seconds)"),
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

    // JavaClass
    JavaClassHelperType("jht", "javaClassHelperType", "Java Class Type"),
    Middleware("mw", "middleware", "Middleware"),
    MemShellType("mst", "memShellType", "MemShell Type"),
    MemShellFunction("msf", "memShellFunction", "MemShell Function"),

    // JavaClass 增强
    // 是否继承 AbstractTranslet
    ExtendsAbstractTranslet("ext", "extendsAbstractTranslet", "Extends AbstractTranslet"),
    // 随机类名的包名
    JavaClassPackageHost("jph", "javaClassPackageHost", "Java Class Package Host");
//    RandomClassName("random", "randomClassName", "Random build class name");

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
