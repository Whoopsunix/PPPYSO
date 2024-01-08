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
        put(CliOptions.Gadget.getLongOpt(), null);
        put(CliOptions.Output.getLongOpt(), null);
        put(CliOptions.SavePath.getLongOpt(), null);

        put(CliOptions.Enchant.getLongOpt(), null);
        put(CliOptions.ExtendsAbstractTranslet.getLongOpt(), false);

        put(CliOptions.Command.getLongOpt(), null);
        put(CliOptions.OS.getLongOpt(), null);
        put(CliOptions.Host.getLongOpt(), null);
        put(CliOptions.Delay.getLongOpt(), null);
        put(CliOptions.DelayTime.getLongOpt(), null);
        put(CliOptions.ServerFilePath.getLongOpt(), null);
        put(CliOptions.LocalFilePath.getLongOpt(), null);
        put(CliOptions.FileContent.getLongOpt(), null);
        put(CliOptions.URL.getLongOpt(), null);
        put(CliOptions.RemoteClassName.getLongOpt(), null);
        put(CliOptions.Constructor.getLongOpt(), null);
        put(CliOptions.LoadFunction.getLongOpt(), null);

        // JavaClass 参数
        put(CliOptions.JavaClassHelperType.getLongOpt(), null);
        put(CliOptions.Middleware.getLongOpt(), null);
        put(CliOptions.MemShellType.getLongOpt(), null);
        put(CliOptions.MemShellFunction.getLongOpt(), null);
    }};

    public static void main(String[] args) throws Exception {
        run(args);
    }

    public static Object run(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(CliOptions.Help.getOpt(), CliOptions.Help.getLongOpt(), false, CliOptions.Help.getDescription());

        options.addOption("g", CliOptions.Gadget.getLongOpt(), true, CliOptions.Gadget.getDescription());
        options.addOption("o", CliOptions.Output.getLongOpt(), true, CliOptions.Output.getDescription());
        options.addOption("save", CliOptions.SavePath.getLongOpt(), true, CliOptions.SavePath.getDescription());

        options.addOption("e", CliOptions.Enchant.getLongOpt(), true, CliOptions.Enchant.getDescription());
        options.addOption("ext", CliOptions.ExtendsAbstractTranslet.getLongOpt(), false, CliOptions.ExtendsAbstractTranslet.getDescription());
        // 附加参数
        options.addOption("cmd", CliOptions.Command.getLongOpt(), true, CliOptions.Command.getDescription());
        options.addOption("os", CliOptions.OS.getLongOpt(), true, CliOptions.OS.getDescription());
        options.addOption("code", CliOptions.Code.getLongOpt(), true, CliOptions.Code.getDescription());
        options.addOption("host", CliOptions.Host.getLongOpt(), true, CliOptions.Host.getDescription());
        options.addOption("d", CliOptions.Delay.getLongOpt(), true, CliOptions.Delay.getDescription());
        options.addOption("dt", CliOptions.DelayTime.getLongOpt(), true, CliOptions.DelayTime.getDescription());
        options.addOption("sfp", CliOptions.ServerFilePath.getLongOpt(), true, CliOptions.ServerFilePath.getDescription());
        options.addOption("lfp", CliOptions.LocalFilePath.getLongOpt(), true, CliOptions.LocalFilePath.getDescription());
        options.addOption("fc", CliOptions.FileContent.getLongOpt(), true, CliOptions.FileContent.getDescription());
        options.addOption("u", CliOptions.URL.getLongOpt(), true, CliOptions.URL.getDescription());
        options.addOption("rcn", CliOptions.RemoteClassName.getLongOpt(), true, CliOptions.RemoteClassName.getDescription());
        options.addOption("ctor", CliOptions.Constructor.getLongOpt(), true, CliOptions.Constructor.getDescription());
        options.addOption("lf", CliOptions.LoadFunction.getLongOpt(), true, CliOptions.LoadFunction.getDescription());

        // javaClass
        options.addOption("jht", CliOptions.JavaClassHelperType.getLongOpt(), true, CliOptions.JavaClassHelperType.getDescription());
        options.addOption("mw", CliOptions.Middleware.getLongOpt(), true, CliOptions.Middleware.getDescription());
        options.addOption("mst", CliOptions.MemShellType.getLongOpt(), true, CliOptions.MemShellType.getDescription());
        options.addOption("msf", CliOptions.MemShellFunction.getLongOpt(), true, CliOptions.MemShellFunction.getDescription());




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

        /**
         * 输出
         */
        if (payloadOptions.get(CliOptions.Output.getLongOpt()) != null) {
            sinksHelper.setOutput(payloadOptions.get(CliOptions.Output.getLongOpt()).toString());
        }

        /**
         * 保存
         */
        if (payloadOptions.get(CliOptions.SavePath.getLongOpt()) != null) {
            sinksHelper.setSave(true);
            sinksHelper.setSavePath(payloadOptions.get(CliOptions.SavePath.getLongOpt()).toString());
        }

        /**
         * 是否继承 AbstractTranslet
         */
        if ((Boolean) payloadOptions.get(CliOptions.ExtendsAbstractTranslet.getLongOpt())) {
            sinksHelper.setExtendsAbstractTranslet((Boolean) payloadOptions.get(CliOptions.ExtendsAbstractTranslet.getLongOpt()));
        }

        String[] enchants = new String[]{};
        if (payloadOptions.get(CliOptions.Enchant.getLongOpt()) != null) {
            String enchant = payloadOptions.get(CliOptions.Enchant.getLongOpt()).toString();
            enchants = enchant.split(",");
        }

        /**
         * 执行命令类
         */
        Boolean isCmd = false;
        // 默认无增强 使用 DEFAULT Runtime
        if (payloadOptions.get(CliOptions.Enchant.getLongOpt()) == null
                || isEnchant(enchants, EnchantType.RUNTIME)) {
            sinksHelper.setEnchant(EnchantType.RUNTIME);
            isCmd = true;
        } else if (isEnchant(enchants, EnchantType.ProcessBuilder)) {
            sinksHelper.setEnchant(EnchantType.ProcessBuilder);
            if (payloadOptions.get(CliOptions.OS.getLongOpt()) != null) {
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
            Object code = payloadOptions.get(CliOptions.Code.getLongOpt());
            if (code != null) {
                sinksHelper.setCode(code.toString());
            } else {
                isCmd = true;
            }
        }

        // 参数检查
        if (isCmd) {
            if (payloadOptions.get(CliOptions.Command.getLongOpt()) != null) {
                sinksHelper.setCommand(payloadOptions.get(CliOptions.Command.getLongOpt()).toString());
            } else {
                Printer.error("Missing Command, use [-cmd | -command] to set");
            }
        }


        if (payloadOptions.get(CliOptions.Enchant.getLongOpt()) == null) {
            return sinksHelper;
        }

        /**
         * 延时
         */
        if (isEnchant(enchants, EnchantType.Delay)) {
            sinksHelper.setEnchant(EnchantType.Delay);
            Object Delay = payloadOptions.get(CliOptions.Delay.getLongOpt());
            if (Delay != null && Delay.toString().equalsIgnoreCase(EnchantType.Timeunit)) {
                sinksHelper.setSleep(EnchantType.Timeunit);
            }
            if (payloadOptions.get(CliOptions.DelayTime.getLongOpt()) != null) {
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
            if (payloadOptions.get(CliOptions.Host.getLongOpt()) != null) {
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
            if (payloadOptions.get(CliOptions.ServerFilePath.getLongOpt()) != null) {
                sinksHelper.setServerFilePath(payloadOptions.get(CliOptions.ServerFilePath.getLongOpt()).toString());
            } else {
                Printer.error("Missing ServerFilePath, use [-sfp | -serverFilePath] to set");
            }
            if (payloadOptions.get(CliOptions.LocalFilePath.getLongOpt()) != null) {
                sinksHelper.setLocalFilePath(payloadOptions.get(CliOptions.LocalFilePath.getLongOpt()).toString());
            } else if (payloadOptions.get(CliOptions.FileContent.getLongOpt()) != null) {
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
            if (payloadOptions.get(CliOptions.URL.getLongOpt()) != null) {
                sinksHelper.setUrl(payloadOptions.get(CliOptions.URL.getLongOpt()).toString());
            } else {
                Printer.error("Missing URL, use [-u | -url] to set");
            }
            if (payloadOptions.get(CliOptions.RemoteClassName.getLongOpt()) != null) {
                sinksHelper.setRemoteClassName(payloadOptions.get(CliOptions.RemoteClassName.getLongOpt()).toString());
            } else {
                Printer.error("Missing RemoteClassName, use [-rcn | -remoteClassName] to set");
            }
            if (payloadOptions.get(CliOptions.Constructor.getLongOpt()) != null) {
                sinksHelper.setConstructor(payloadOptions.get(CliOptions.Constructor.getLongOpt()).toString());
            }
        }

        /**
         * JavaClass
         */
        Boolean isJavaClass = false;
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        if (isEnchant(enchants, EnchantType.JavaClass)) {
            sinksHelper.setEnchant(EnchantType.JavaClass);
            // 组件
            if (payloadOptions.get(CliOptions.Middleware.getLongOpt()) != null) {
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
            if (payloadOptions.get(CliOptions.JavaClassHelperType.getLongOpt()) != null) {
                // 内存马
                if (payloadOptions.get(CliOptions.JavaClassHelperType.getLongOpt()).toString().equalsIgnoreCase(JavaClassHelperType.MemShell)) {
                    javaClassHelper.setJavaClassHelperType(JavaClassHelperType.MemShell);
                    // 内存马类型
                    if (payloadOptions.get(CliOptions.MemShellType.getLongOpt()) != null) {
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
                    if (payloadOptions.get(CliOptions.MemShellFunction.getLongOpt()) != null) {
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
                sinksHelper.setJavaClassHelper(javaClassHelper);
            } else {
                Printer.error("Missing JavaClassHelperType, use [-jht | -javaClassHelperType] to set");
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
            if (payloadOptions.get(CliOptions.LoadFunction.getLongOpt()) != null) {
                if (payloadOptions.get(CliOptions.LoadFunction.getLongOpt()).toString().equalsIgnoreCase(EnchantType.RHINO)) {
                    sinksHelper.setLoadFunction(EnchantType.RHINO);
                }
            }
        }


        return sinksHelper;
    }

    public static Boolean isEnchant(String[] enchants, String enchant) {
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

    }
}
