package com.ppp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Java Class Loader 类型
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CliOptions {
    String Help = "help";
    // 基本参数
    String Gadget = "gadget";
    String Output = "output";
    String SavePath = "savePath";
    // 增强类型
    String Enchant = "enchant";
    String ExtendsAbstractTranslet = "extendsAbstractTranslet";

    // 命令执行内容
    String Command = "command";
    // 操作系统
    String OS = "os";
    // Socket 探测域名
    String Host = "host";
    // 线程延时类型
    String Delay = "delay";
    // 线程延时时间
    String DelayTime = "delayTime";
    // 服务器文件路径
    String ServerFilePath = "serverFilePath";
    // 本地文件路径
    String LocalFilePath = "localFilePath";
    // 文件内容
    String FileContent = "fileContent";
    // 远程加载 url
    String URL = "url";
    // 远程加载类名
    String RemoteClassName = "remoteClassName";
    // 远程加载构造方法参数
    String Constructor = "constructor";
    // 本地加载方法
    String LoadFunction = "loadFunction";
    String JavaClassHelperType = "javaClassHelperType";
    String Middleware = "middleware";
    String MemShell = "memShell";
    String MemShellFunction = "memShellFunction";

    String value();
}
