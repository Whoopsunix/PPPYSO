package com.ppp;

import com.ppp.annotation.*;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.ClassUtils;
import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author Whoopsunix
 */
public class CliScheduler {
    private static String gadgetPackageName = "com.ppp.chain";
    public static HashMap<String, Object> payloadOptions = new HashMap<String, Object>() {{
//        put(CliOptions.Gadget.getLongOpt(), null);
//        put(CliOptions.Output.getLongOpt(), null);
//        put(CliOptions.SavePath.getLongOpt(), null);
//
//        put(CliOptions.Enchant.getLongOpt(), null);
//        put(CliOptions.ExtendsAbstractTranslet.getLongOpt(), false);
//
//        put(CliOptions.Command.getLongOpt(), null);
//        put(CliOptions.OS.getLongOpt(), null);
//        put(CliOptions.Host.getLongOpt(), null);
//        put(CliOptions.Delay.getLongOpt(), null);
//        put(CliOptions.DelayTime.getLongOpt(), null);
//        put(CliOptions.ServerFilePath.getLongOpt(), null);
//        put(CliOptions.LocalFilePath.getLongOpt(), null);
//        put(CliOptions.FileContent.getLongOpt(), null);
//        put(CliOptions.URL.getLongOpt(), null);
//        put(CliOptions.RemoteClassName.getLongOpt(), null);
//        put(CliOptions.Constructor.getLongOpt(), null);
//        put(CliOptions.LoadFunction.getLongOpt(), null);
//
//        // JavaClass 参数
//        put(CliOptions.JavaClassHelperType.getLongOpt(), null);
//        put(CliOptions.Middleware.getLongOpt(), null);
//        put(CliOptions.MemShellType.getLongOpt(), null);
//        put(CliOptions.MemShellFunction.getLongOpt(), null);
//        put(CliOptions.JavaClassPackageHost.getLongOpt(), null);
//        put(CliOptions.JavaClassFilePath.getLongOpt(), null);
    }};

    public static void main(String[] args) throws Exception {
        run(args);
    }

    public static Object run(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(CliOptions.Help.getOpt(), CliOptions.Help.getLongOpt(), false, CliOptions.Help.getDescription());

        options.addOption(CliOptions.Gadget.getOpt(), CliOptions.Gadget.getLongOpt(), true, CliOptions.Gadget.getDescription());
        options.addOption(CliOptions.Output.getOpt(), CliOptions.Output.getLongOpt(), true, CliOptions.Output.getDescription());
        options.addOption(CliOptions.SavePath.getOpt(), CliOptions.SavePath.getLongOpt(), true, CliOptions.SavePath.getDescription());
        options.addOption(CliOptions.ClosePrinter.getOpt(), CliOptions.ClosePrinter.getLongOpt(), false, CliOptions.ClosePrinter.getDescription());

        options.addOption(CliOptions.Enchant.getOpt(), CliOptions.Enchant.getLongOpt(), true, CliOptions.Enchant.getDescription());
        options.addOption(CliOptions.ExtendsAbstractTranslet.getOpt(), CliOptions.ExtendsAbstractTranslet.getLongOpt(), false, CliOptions.ExtendsAbstractTranslet.getDescription());
        // 附加参数
        options.addOption(CliOptions.Command.getOpt(), CliOptions.Command.getLongOpt(), true, CliOptions.Command.getDescription());
        options.addOption(CliOptions.OS.getOpt(), CliOptions.OS.getLongOpt(), true, CliOptions.OS.getDescription());
        options.addOption(CliOptions.Code.getOpt(), CliOptions.Code.getLongOpt(), true, CliOptions.Code.getDescription());
        options.addOption(CliOptions.CodeFile.getOpt(), CliOptions.CodeFile.getLongOpt(), true, CliOptions.CodeFile.getDescription());
        options.addOption(CliOptions.Host.getOpt(), CliOptions.Host.getLongOpt(), true, CliOptions.Host.getDescription());
        options.addOption(CliOptions.Delay.getOpt(), CliOptions.Delay.getLongOpt(), true, CliOptions.Delay.getDescription());
        options.addOption(CliOptions.DelayTime.getOpt(), CliOptions.DelayTime.getLongOpt(), true, CliOptions.DelayTime.getDescription());
        options.addOption(CliOptions.ServerFilePath.getOpt(), CliOptions.ServerFilePath.getLongOpt(), true, CliOptions.ServerFilePath.getDescription());
        options.addOption(CliOptions.LocalFilePath.getOpt(), CliOptions.LocalFilePath.getLongOpt(), true, CliOptions.LocalFilePath.getDescription());
        options.addOption(CliOptions.FileContent.getOpt(), CliOptions.FileContent.getLongOpt(), true, CliOptions.FileContent.getDescription());
        options.addOption(CliOptions.URL.getOpt(), CliOptions.URL.getLongOpt(), true, CliOptions.URL.getDescription());
        options.addOption(CliOptions.RemoteClassName.getOpt(), CliOptions.RemoteClassName.getLongOpt(), true, CliOptions.RemoteClassName.getDescription());
        options.addOption(CliOptions.Constructor.getOpt(), CliOptions.Constructor.getLongOpt(), true, CliOptions.Constructor.getDescription());
        options.addOption(CliOptions.LoadFunction.getOpt(), CliOptions.LoadFunction.getLongOpt(), true, CliOptions.LoadFunction.getDescription());

        // javaClass
        options.addOption(CliOptions.JavaClassHelperType.getOpt(), CliOptions.JavaClassHelperType.getLongOpt(), true, CliOptions.JavaClassHelperType.getDescription());
        options.addOption(CliOptions.Middleware.getOpt(), CliOptions.Middleware.getLongOpt(), true, CliOptions.Middleware.getDescription());
        options.addOption(CliOptions.MemShellType.getOpt(), CliOptions.MemShellType.getLongOpt(), true, CliOptions.MemShellType.getDescription());
        options.addOption(CliOptions.MemShellFunction.getOpt(), CliOptions.MemShellFunction.getLongOpt(), true, CliOptions.MemShellFunction.getDescription());
        options.addOption(CliOptions.JavaClassPackageHost.getOpt(), CliOptions.JavaClassPackageHost.getLongOpt(), true, CliOptions.JavaClassPackageHost.getDescription());
        options.addOption(CliOptions.JavaClassFilePath.getOpt(), CliOptions.JavaClassFilePath.getLongOpt(), true, CliOptions.JavaClassFilePath.getDescription());

        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.hasOption(CliOptions.Help.getLongOpt())) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar PPPYSO-cli-jar-with-dependencies.jar", options, true);
        }

        // 初始化
        initPayloadOptions(commandLine);

        Object gadget = payloadOptions.get(CliOptions.Gadget.getLongOpt());
        if (gadget == null) {
            Printer.error("Missing Gadget, use [-g | -gadget] to set");
        }

        Class<? extends ObjectPayload> gadgetClass = getGadgetClass(gadget.toString());
        if (gadgetClass == null) {
            Printer.error("No such gadget: " + payloadOptions.get(CliOptions.Gadget.getLongOpt()));
        }

        SinksHelper sinksHelper = initSinkHelper(gadgetClass);

        return ObjectPayloadBuilder.builder(gadgetClass, sinksHelper);

    }

    public static Class<? extends ObjectPayload> getGadgetClass(String gadget) throws Exception {
        // 调用链检查
        List<Class<?>> classes = ClassUtils.getClasses(gadgetPackageName);
        for (Class<?> clazz : classes) {
            String className = clazz.getSimpleName();
            if (className.equalsIgnoreCase(gadget)) {
                return (Class<? extends ObjectPayload>) clazz;
            }
        }
        return null;
    }

    public static SinksHelper initSinkHelper(Class<? extends ObjectPayload> gadgetClass) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        String[] sinks = (String[]) Reflections.invokeMethod(gadgetClass.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
        sinksHelper.setSink(sinks[0]);
        JavaClassHelper javaClassHelper = new JavaClassHelper();

        if (payloadOptions.containsKey(CliOptions.ClosePrinter.getLongOpt())) {
            System.setProperty(Printer.PRINT_ENABLED_PROPERTY, String.valueOf(false));
        }

        /**
         * 输出
         */
        if (payloadOptions.containsKey(CliOptions.Output.getLongOpt())) {
            sinksHelper.setOutput(payloadOptions.get(CliOptions.Output.getLongOpt()).toString());
        }

        /**
         * 保存
         */
        if (payloadOptions.containsKey(CliOptions.SavePath.getLongOpt())) {
            sinksHelper.setSave(true);
            sinksHelper.setSavePath(payloadOptions.get(CliOptions.SavePath.getLongOpt()).toString());
        }

        String[] enchants = new String[]{};
        if (payloadOptions.containsKey(CliOptions.Enchant.getLongOpt())) {
            String enchant = payloadOptions.get(CliOptions.Enchant.getLongOpt()).toString();
            enchants = enchant.split(",");
        }

        /**
         * 是否继承 AbstractTranslet
         */
        if (payloadOptions.containsKey(CliOptions.ExtendsAbstractTranslet.getLongOpt()) && (Boolean) payloadOptions.get(CliOptions.ExtendsAbstractTranslet.getLongOpt())) {
            javaClassHelper.setExtendsAbstractTranslet((Boolean) payloadOptions.get(CliOptions.ExtendsAbstractTranslet.getLongOpt()));
        }
        /**
         * 是否修改 package host
         */
        if (payloadOptions.containsKey(CliOptions.JavaClassPackageHost.getLongOpt())) {
            javaClassHelper.setJavaClassPackageHost(payloadOptions.get(CliOptions.JavaClassPackageHost.getLongOpt()).toString());
        }

        /**
         * 执行命令类
         */
        Boolean isCmd = false;
        // 默认无增强 使用 DEFAULT Runtime
        if (!payloadOptions.containsKey(CliOptions.Enchant.getLongOpt())
                || isEnchant(enchants, EnchantType.RUNTIME)) {
            sinksHelper.setEnchant(EnchantType.RUNTIME);
            isCmd = true;
        } else if (isEnchant(enchants, EnchantType.ProcessBuilder)) {
            sinksHelper.setEnchant(EnchantType.ProcessBuilder);
            if (payloadOptions.containsKey(CliOptions.OS.getLongOpt())) {
                if (payloadOptions.get(CliOptions.OS.getLongOpt()).toString().equalsIgnoreCase(EnchantType.WIN))
                    sinksHelper.setOs(EnchantType.WIN);
            } else {
                Printer.warn("Not supported OS, use [-os] to set");
            }
            isCmd = true;
        }

        /**
         * 代码执行
         */
        if (isEnchant(enchants, EnchantType.ScriptEngine)) {
            sinksHelper.setEnchant(EnchantType.ScriptEngine);
            if (payloadOptions.containsKey(CliOptions.Code.getLongOpt())) {
                sinksHelper.setCode(payloadOptions.get(CliOptions.Code.getLongOpt()).toString());
            } else if (payloadOptions.containsKey(CliOptions.CodeFile.getLongOpt())) {
                sinksHelper.setCodeFile(payloadOptions.get(CliOptions.CodeFile.getLongOpt()).toString());
            } else {
                // 默认 Runtime 执行
                isCmd = true;
            }
        }

        // 参数检查
        if (isCmd) {
            if (payloadOptions.containsKey(CliOptions.Command.getLongOpt())) {
                sinksHelper.setCommand(payloadOptions.get(CliOptions.Command.getLongOpt()).toString());
            } else {
                Printer.error("Missing Command, use [-cmd | -command] to set");
            }
        }


//        if (payloadOptions.get(CliOptions.Enchant.getLongOpt()) == null) {
//            return sinksHelper;
//        }

        /**
         * 延时
         */
        if (isEnchant(enchants, EnchantType.Delay)) {
            sinksHelper.setEnchant(EnchantType.Delay);
            if (payloadOptions.containsKey(CliOptions.Delay.getLongOpt()) &&
                    payloadOptions.get(CliOptions.Delay.getLongOpt()).toString().equalsIgnoreCase(EnchantType.Timeunit)) {
                sinksHelper.setSleep(EnchantType.Timeunit);
            }
            if (payloadOptions.containsKey(CliOptions.DelayTime.getLongOpt())) {
                sinksHelper.setSleepTime((Long) payloadOptions.get(CliOptions.DelayTime.getLongOpt()));
            } else {
                Printer.error("Missing DelayTime, use [-dt | -delayTime] to set");
            }
        }

        /**
         * Socket 探测
         */
        if (isEnchant(enchants, EnchantType.Socket)) {
            sinksHelper.setEnchant(EnchantType.Socket);
            if (payloadOptions.containsKey(CliOptions.Host.getLongOpt())) {
                sinksHelper.setHost(payloadOptions.get(CliOptions.Host.getLongOpt()).toString());
            } else {
                Printer.error("Missing Host, use [-host] to set");
            }
        }

        /**
         * 文件写入
         */
        if (isEnchant(enchants, EnchantType.FileWrite)) {
            sinksHelper.setEnchant(EnchantType.FileWrite);
            if (payloadOptions.containsKey(CliOptions.ServerFilePath.getLongOpt())) {
                sinksHelper.setServerFilePath(payloadOptions.get(CliOptions.ServerFilePath.getLongOpt()).toString());
            } else {
                Printer.error("Missing ServerFilePath, use [-sfp | -serverFilePath] to set");
            }
            if (payloadOptions.containsKey(CliOptions.LocalFilePath.getLongOpt())) {
                sinksHelper.setLocalFilePath(payloadOptions.get(CliOptions.LocalFilePath.getLongOpt()).toString());
            } else if (payloadOptions.containsKey(CliOptions.FileContent.getLongOpt())) {
                sinksHelper.setFileContent(payloadOptions.get(CliOptions.FileContent.getLongOpt()).toString());
            } else {
                Printer.error("Missing LocalFilePath or FileContent, use [-lfp | -localFilePath] or [-fc | -fileContent] to set");
            }
        }

        /**
         * 远程加载
         */
        if (isEnchant(enchants, EnchantType.RemoteLoad)) {
            sinksHelper.setEnchant(EnchantType.RemoteLoad);
            if (payloadOptions.containsKey(CliOptions.URL.getLongOpt())) {
                sinksHelper.setUrl(payloadOptions.get(CliOptions.URL.getLongOpt()).toString());
            } else {
                Printer.error("Missing URL, use [-u | -url] to set");
            }
            if (payloadOptions.containsKey(CliOptions.RemoteClassName.getLongOpt())) {
                sinksHelper.setRemoteClassName(payloadOptions.get(CliOptions.RemoteClassName.getLongOpt()).toString());
            } else {
                Printer.error("Missing RemoteClassName, use [-rcn | -remoteClassName] to set");
            }
            if (payloadOptions.containsKey(CliOptions.Constructor.getLongOpt())) {
                sinksHelper.setConstructor(payloadOptions.get(CliOptions.Constructor.getLongOpt()).toString());
            }
        }

        /**
         * JavaClass
         */
        Boolean isJavaClass = false;
        if (isEnchant(enchants, EnchantType.JavaClass)) {
            sinksHelper.setEnchant(EnchantType.JavaClass);
            /**
             * 自定义
             */
            if (payloadOptions.containsKey(CliOptions.JavaClassFilePath.getLongOpt())) {
                javaClassHelper.setJavaClassHelperType(JavaClassHelperType.Custom);
                javaClassHelper.setJavaClassFilePath(payloadOptions.get(CliOptions.JavaClassFilePath.getLongOpt()).toString());
            } else {
                /**
                 * 内置
                 */
                if (payloadOptions.containsKey(CliOptions.Middleware.getLongOpt())) {
                    // 组件
                    if (payloadOptions.get(CliOptions.Middleware.getLongOpt()).toString().equalsIgnoreCase(Middleware.Tomcat)) {
                        javaClassHelper.setMiddleware(Middleware.Tomcat);
                    } else if (payloadOptions.get(CliOptions.Middleware.getLongOpt()).toString().equalsIgnoreCase(Middleware.Jetty)) {
                        javaClassHelper.setMiddleware(Middleware.Jetty);
                    } else {
                        Printer.error("The Middleware is not supported");
                    }
                } else {
                    Printer.error("Missing Middleware, use [-mw | -middleware] to set");
                }

                // JavaClass 类型
                if (payloadOptions.containsKey(CliOptions.JavaClassHelperType.getLongOpt())) {
                    // 内存马
                    if (payloadOptions.get(CliOptions.JavaClassHelperType.getLongOpt()).toString().equalsIgnoreCase(JavaClassHelperType.MemShell)) {
                        javaClassHelper.setJavaClassHelperType(JavaClassHelperType.MemShell);
                        // 内存马类型
                        if (payloadOptions.containsKey(CliOptions.MemShellType.getLongOpt())) {
                            if (payloadOptions.get(CliOptions.MemShellType.getLongOpt()).toString().equalsIgnoreCase(MemShell.Listener)) {
                                javaClassHelper.setMemShell(MemShell.Listener);
                            } else if (payloadOptions.get(CliOptions.MemShellType.getLongOpt()).toString().equalsIgnoreCase(MemShell.Servlet)) {
                                javaClassHelper.setMemShell(MemShell.Servlet);
                            } else {
                                Printer.error("The MemShell is not supported");
                            }
                        } else {
                            Printer.error("Missing MemShell, use [-mst | -memShellType] to set");
                        }

                        // 内存马功能
                        if (payloadOptions.containsKey(CliOptions.MemShellFunction.getLongOpt())) {
                            if (payloadOptions.get(CliOptions.MemShellFunction.getLongOpt()).toString().equalsIgnoreCase(MemShellFunction.Godzilla)) {
                                javaClassHelper.setMemShellFunction(MemShellFunction.Godzilla);
                            } else if (payloadOptions.get(CliOptions.MemShellFunction.getLongOpt()).toString().equalsIgnoreCase(MemShellFunction.Runtime)) {
                                javaClassHelper.setMemShellFunction(MemShellFunction.Runtime);
                            }
                        } else {
                            Printer.error("Missing MemShellFunction, use [-msf | -memShellFunction] to set");
                        }
                    }
                    // RCE 回显
                    else if (payloadOptions.get(CliOptions.JavaClassHelperType.getLongOpt()).toString().equalsIgnoreCase(JavaClassHelperType.RceEcho)) {
                        javaClassHelper.setJavaClassHelperType(JavaClassHelperType.RceEcho);
                    } else {
                        Printer.error("The JavaClassHelperType is not supported");
                    }
                } else {
                    Printer.error("Missing JavaClassHelperType, use [-jht | -javaClassHelperType] to set");
                }
            }
            isJavaClass = true;
        }

        /**
         * 本地字节码加载
         */
        if (isEnchant(enchants, EnchantType.LocalLoad)) {
            sinksHelper.setEnchant(EnchantType.LocalLoad);
            sinksHelper.setJavaClassHelper(javaClassHelper);
            if (!isJavaClass) {
                Printer.error("Missing JavaClass, use [-e | -enchant] to set javaClass");
            }
//            if (payloadOptions.get(CliOptions.LoadFunction.getLongOpt()) != null) {
            if (payloadOptions.containsKey(CliOptions.LoadFunction.getLongOpt())) {
                if (payloadOptions.get(CliOptions.LoadFunction.getLongOpt()).toString().equalsIgnoreCase(EnchantType.RHINO)) {
                    sinksHelper.setLoadFunction(EnchantType.RHINO);
                }
            }
        }
        sinksHelper.setJavaClassHelper(javaClassHelper);


        return sinksHelper;
    }

    public static Boolean isEnchant(String[] enchants, String enchant) {
        if (enchants == null) {
            return false;
        }
        for (String e : enchants) {
            if (e.equalsIgnoreCase(enchant)) {
                return true;
            }
        }
        return false;
    }


    public static void initPayloadOptions(CommandLine commandLine) {
        if (commandLine.hasOption(CliOptions.Gadget.getLongOpt())) {
            payloadOptions.put(CliOptions.Gadget.getLongOpt(), commandLine.getOptionValue(CliOptions.Gadget.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.Output.getLongOpt())) {
            payloadOptions.put(CliOptions.Output.getLongOpt(), commandLine.getOptionValue(CliOptions.Output.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.SavePath.getLongOpt())) {
            payloadOptions.put(CliOptions.SavePath.getLongOpt(), commandLine.getOptionValue(CliOptions.SavePath.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.ClosePrinter.getOpt())) {
            payloadOptions.put(CliOptions.ClosePrinter.getLongOpt(), commandLine.getOptionValue(CliOptions.ClosePrinter.getLongOpt()));
        }

        if (commandLine.hasOption(CliOptions.Enchant.getLongOpt())) {
            payloadOptions.put(CliOptions.Enchant.getLongOpt(), commandLine.getOptionValue(CliOptions.Enchant.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.ExtendsAbstractTranslet.getLongOpt())) {
            payloadOptions.put(CliOptions.ExtendsAbstractTranslet.getLongOpt(), true);
        }

        if (commandLine.hasOption(CliOptions.Command.getLongOpt())) {
            payloadOptions.put(CliOptions.Command.getLongOpt(), commandLine.getOptionValue(CliOptions.Command.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.OS.getLongOpt())) {
            payloadOptions.put(CliOptions.OS.getLongOpt(), commandLine.getOptionValue(CliOptions.OS.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.Code.getLongOpt())) {
            payloadOptions.put(CliOptions.Code.getLongOpt(), commandLine.getOptionValue(CliOptions.Code.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.CodeFile.getLongOpt())) {
            payloadOptions.put(CliOptions.CodeFile.getLongOpt(), commandLine.getOptionValue(CliOptions.CodeFile.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.Host.getLongOpt())) {
            payloadOptions.put(CliOptions.Host.getLongOpt(), commandLine.getOptionValue(CliOptions.Host.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.Delay.getLongOpt())) {
            payloadOptions.put(CliOptions.Delay.getLongOpt(), commandLine.getOptionValue(CliOptions.Delay.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.DelayTime.getLongOpt())) {
            payloadOptions.put(CliOptions.DelayTime.getLongOpt(), Long.parseLong(commandLine.getOptionValue(CliOptions.DelayTime.getLongOpt())));
        }
        if (commandLine.hasOption(CliOptions.ServerFilePath.getLongOpt())) {
            payloadOptions.put(CliOptions.ServerFilePath.getLongOpt(), commandLine.getOptionValue(CliOptions.ServerFilePath.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.LocalFilePath.getLongOpt())) {
            payloadOptions.put(CliOptions.LocalFilePath.getLongOpt(), commandLine.getOptionValue(CliOptions.LocalFilePath.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.FileContent.getLongOpt())) {
            payloadOptions.put(CliOptions.FileContent.getLongOpt(), commandLine.getOptionValue(CliOptions.FileContent.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.URL.getLongOpt())) {
            payloadOptions.put(CliOptions.URL.getLongOpt(), commandLine.getOptionValue(CliOptions.URL.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.RemoteClassName.getLongOpt())) {
            payloadOptions.put(CliOptions.RemoteClassName.getLongOpt(), commandLine.getOptionValue(CliOptions.RemoteClassName.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.Constructor.getLongOpt())) {
            payloadOptions.put(CliOptions.Constructor.getLongOpt(), commandLine.getOptionValue(CliOptions.Constructor.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.LoadFunction.getLongOpt())) {
            payloadOptions.put(CliOptions.LoadFunction.getLongOpt(), commandLine.getOptionValue(CliOptions.LoadFunction.getLongOpt()));
        }

        if (commandLine.hasOption(CliOptions.JavaClassHelperType.getLongOpt())) {
            payloadOptions.put(CliOptions.JavaClassHelperType.getLongOpt(), commandLine.getOptionValue(CliOptions.JavaClassHelperType.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.Middleware.getLongOpt())) {
            payloadOptions.put(CliOptions.Middleware.getLongOpt(), commandLine.getOptionValue(CliOptions.Middleware.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.MemShellType.getLongOpt())) {
            payloadOptions.put(CliOptions.MemShellType.getLongOpt(), commandLine.getOptionValue(CliOptions.MemShellType.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.MemShellFunction.getLongOpt())) {
            payloadOptions.put(CliOptions.MemShellFunction.getLongOpt(), commandLine.getOptionValue(CliOptions.MemShellFunction.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.JavaClassPackageHost.getLongOpt())) {
            payloadOptions.put(CliOptions.JavaClassPackageHost.getLongOpt(), commandLine.getOptionValue(CliOptions.JavaClassPackageHost.getLongOpt()));
        }
        if (commandLine.hasOption(CliOptions.JavaClassFilePath.getLongOpt())) {
            payloadOptions.put(CliOptions.JavaClassFilePath.getLongOpt(), commandLine.getOptionValue(CliOptions.JavaClassFilePath.getLongOpt()));
        }

    }
}
