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
        put(CliOptions.Gadget, null);
        put(CliOptions.Output, null);
        put(CliOptions.SavePath, null);

        put(CliOptions.Enchant, null);
        put(CliOptions.ExtendsAbstractTranslet, false);

        put(CliOptions.Command, null);
        put(CliOptions.OS, null);
        put(CliOptions.Host, null);
        put(CliOptions.Delay, null);
        put(CliOptions.DelayTime, null);
        put(CliOptions.ServerFilePath, null);
        put(CliOptions.LocalFilePath, null);
        put(CliOptions.FileContent, null);
        put(CliOptions.URL, null);
        put(CliOptions.RemoteClassName, null);
        put(CliOptions.Constructor, null);
        put(CliOptions.LoadFunction, null);

        // JavaClass 参数
        put(CliOptions.JavaClassHelperType, null);
        put(CliOptions.Middleware, null);
        put(CliOptions.MemShell, null);
        put(CliOptions.MemShellFunction, null);
    }};

    public static void main(String[] args) throws Exception {
        run(args);
    }

    public static void run(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", CliOptions.Help, false, "Print this usage information");

        options.addOption("g", CliOptions.Gadget, true, "Gadget class name");
        options.addOption("o", CliOptions.Output, true, "Gadget output type");
        options.addOption("sp", CliOptions.SavePath, true, "Save payload to file");

        options.addOption("e", CliOptions.Enchant, true, "Enchant type");
        options.addOption("ext", CliOptions.ExtendsAbstractTranslet, false, "Extends AbstractTranslet");
        // 附加参数
        options.addOption("cmd", CliOptions.Command, true, "Command");
        options.addOption("os", CliOptions.OS, true, "OS");
        options.addOption("host", CliOptions.Host, true, "Host");
        options.addOption("d", CliOptions.Delay, true, "Delay function, default is Thread.sleep");
        options.addOption("dt", CliOptions.DelayTime, true, "Delay time (in seconds)");
        options.addOption("sfp", CliOptions.ServerFilePath, true, "Server file path");
        options.addOption("lfp", CliOptions.LocalFilePath, true, "Local file path");
        options.addOption("fc", CliOptions.FileContent, true, "File content");
        options.addOption("u", CliOptions.URL, true, "URL");
        options.addOption("rcn", CliOptions.RemoteClassName, true, "Remote Load Class Name");
        options.addOption("ctor", CliOptions.Constructor, true, "Constructor param");
        options.addOption("lf", CliOptions.LoadFunction, true, "Load function");

        // javaClass
        options.addOption("jht", CliOptions.JavaClassHelperType, true, "Java Class Type");
        options.addOption("mw", CliOptions.Middleware, true, "Middleware");
        options.addOption("ms", CliOptions.MemShell, true, "MemShell Type");
        options.addOption("msf", CliOptions.MemShellFunction, true, "MemShell Function");


        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        if (commandLine.hasOption(CliOptions.Help)) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar PPPYSO-cli-jar-with-dependencies.jar", options, true);
        }

        // 初始化
        initPayloadOptions(commandLine);

        Class<? extends ObjectPayload> gadgetClass = null;

        // 调用链检查
        List<Class<?>> classes = ClassUtils.getClasses(gadgetPackageName);
        for (Class<?> clazz : classes) {
            String className = clazz.getSimpleName();
            Object gadget = payloadOptions.get(CliOptions.Gadget);
            if (gadget != null && className.equalsIgnoreCase(gadget.toString())) {
                gadgetClass = (Class<? extends ObjectPayload>) clazz;
                break;
            }
        }
        if (gadgetClass == null) {
            Printer.error("No such gadget: " + payloadOptions.get(CliOptions.Gadget));
        }

        SinksHelper sinksHelper = initSinkHelper(gadgetClass);

        Object gadget = ObjectPayloadBuilder.builder(gadgetClass, sinksHelper);

    }

    public static SinksHelper initSinkHelper(Class<? extends ObjectPayload> gadgetClass) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        String[] sinks = (String[]) Reflections.invokeMethod(gadgetClass.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
        sinksHelper.setSink(sinks[0]);

        /**
         * 输出
         */
        if (payloadOptions.get(CliOptions.Output) != null) {
            if (payloadOptions.get(CliOptions.Output).toString().equalsIgnoreCase(Save.GZIP)) {
                sinksHelper.setOutput(Save.GZIP);
            } else if (payloadOptions.get(CliOptions.Output).toString().equalsIgnoreCase(Save.Base64)) {
                sinksHelper.setOutput(Save.Base64);
            } else if (payloadOptions.get(CliOptions.Output).toString().equalsIgnoreCase(Save.Base64gzip)) {
                sinksHelper.setOutput(Save.Base64gzip);
            } else if (payloadOptions.get(CliOptions.Output).toString().equalsIgnoreCase(Save.XStream)) {
                sinksHelper.setOutput(Save.XStream);
            } else if (payloadOptions.get(CliOptions.Output).toString().equalsIgnoreCase(Save.hexAscii)) {
                sinksHelper.setOutput(Save.hexAscii);
            }
        }

        /**
         * 保存
         */
        if (payloadOptions.get(CliOptions.SavePath) != null) {
            sinksHelper.setSave(true);
            sinksHelper.setSavePath(payloadOptions.get(CliOptions.SavePath).toString());
        }

        /**
         * 是否继承 AbstractTranslet
         */
        if (payloadOptions.get(CliOptions.ExtendsAbstractTranslet) != null) {
            sinksHelper.setExtendsAbstractTranslet((Boolean) payloadOptions.get(CliOptions.ExtendsAbstractTranslet));
        }

        /**
         * 执行命令类
         */
        Boolean commandParam = false;
        // 默认无增强 使用 DEFAULT Runtime
        if (payloadOptions.get(CliOptions.Enchant) == null
                || payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.RUNTIME)) {
            sinksHelper.setEnchant(EnchantType.DEFAULT);
            commandParam = true;
        } else if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.ProcessBuilder)) {
            sinksHelper.setEnchant(EnchantType.ProcessBuilder);
            if (payloadOptions.get(CliOptions.OS) != null) {
                if (payloadOptions.get(CliOptions.OS).toString().equalsIgnoreCase(EnchantType.WIN))
                    sinksHelper.setOs(EnchantType.WIN);
            }
            commandParam = true;
        } else if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.ScriptEngine)) {
            sinksHelper.setEnchant(EnchantType.ScriptEngine);
            commandParam = true;
        }
        // 参数检查
        if (commandParam && payloadOptions.get(CliOptions.Command) != null) {
            sinksHelper.setCommand(payloadOptions.get(CliOptions.Command).toString());
        } else {
            Printer.error("Missing Command, use [-cmd | -command] to set");
        }

        if (payloadOptions.get(CliOptions.Enchant) == null) {
            return sinksHelper;
        }

        /**
         * 延时
         */
        if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.Delay)) {
            sinksHelper.setEnchant(EnchantType.Delay);
            if (payloadOptions.get(CliOptions.Delay).toString().equalsIgnoreCase(EnchantType.Timeunit)) {
                sinksHelper.setSleep(EnchantType.Timeunit);
            }
            if (payloadOptions.get(CliOptions.DelayTime) != null) {
                sinksHelper.setSleepTime((Long) payloadOptions.get(CliOptions.DelayTime));
            } else {
                Printer.error("Missing DelayTime, use [-dt | -delayTime] to set");
            }
        }

        /**
         * Socket 探测
         */
        if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.Socket)) {
            sinksHelper.setEnchant(EnchantType.Socket);
            if (payloadOptions.get(CliOptions.Host) != null) {
                sinksHelper.setHost(payloadOptions.get(CliOptions.Host).toString());
            } else {
                Printer.error("Missing Host, use [-host] to set");
            }
        }

        /**
         * 文件写入
         */
        if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.FileWrite)) {
            sinksHelper.setEnchant(EnchantType.FileWrite);
            if (payloadOptions.get(CliOptions.ServerFilePath) != null) {
                sinksHelper.setServerFilePath(payloadOptions.get(CliOptions.ServerFilePath).toString());
            } else {
                Printer.error("Missing ServerFilePath, use [-sfp | -serverFilePath] to set");
            }
            if (payloadOptions.get(CliOptions.LocalFilePath) != null) {
                sinksHelper.setLocalFilePath(payloadOptions.get(CliOptions.LocalFilePath).toString());
            } else if (payloadOptions.get(CliOptions.FileContent) != null) {
                sinksHelper.setFileContent(payloadOptions.get(CliOptions.FileContent).toString());
            }
        }

        /**
         * 远程加载
         */
        if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.RemoteLoad)) {
            sinksHelper.setEnchant(EnchantType.RemoteLoad);
            if (payloadOptions.get(CliOptions.RemoteClassName) != null) {
                sinksHelper.setRemoteClassName(payloadOptions.get(CliOptions.RemoteClassName).toString());
            } else {
                Printer.error("Missing RemoteClassName, use [-rcn | -remoteClassName] to set");
            }
            if (payloadOptions.get(CliOptions.URL) != null) {
                sinksHelper.setUrl(payloadOptions.get(CliOptions.URL).toString());
            } else {
                Printer.error("Missing URL, use [-u | -url] to set");
            }
            if (payloadOptions.get(CliOptions.Constructor) != null) {
                sinksHelper.setConstructor(payloadOptions.get(CliOptions.Constructor).toString());
            }
        }

        /**
         * JavaClass
         */
        Boolean isJavaClass = false;
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.JavaClass)) {
            // 组件
            if (payloadOptions.get(CliOptions.Middleware) != null) {
                if (payloadOptions.get(CliOptions.Middleware).toString().equalsIgnoreCase(Middleware.Tomcat)) {
                    javaClassHelper.setMiddleware(Middleware.Tomcat);
                } else if (payloadOptions.get(CliOptions.Middleware).toString().equalsIgnoreCase(Middleware.Jetty)) {
                    javaClassHelper.setMiddleware(Middleware.Jetty);
                } else {
                    Printer.error("The Middleware is not supported");
                }
            } else {
                Printer.error("Missing Middleware, use [-mw | -middleware] to set");
            }

            // JavaClass 类型
            if (payloadOptions.get(CliOptions.JavaClassHelperType) != null) {
                // 内存马
                if (payloadOptions.get(CliOptions.JavaClassHelperType).toString().equalsIgnoreCase(JavaClassHelperType.MemShell)) {
                    javaClassHelper.setJavaClassHelperType(JavaClassHelperType.MemShell);
                    // 内存马类型
                    if (payloadOptions.get(CliOptions.MemShell) != null) {
                        if (payloadOptions.get(CliOptions.MemShell).toString().equalsIgnoreCase(MemShell.Listener)) {
                            javaClassHelper.setMemShell(MemShell.Listener);
                        } else if (payloadOptions.get(CliOptions.MemShell).toString().equalsIgnoreCase(MemShell.Servlet)) {
                            javaClassHelper.setMemShell(MemShell.Servlet);
                        } else {
                            Printer.error("The MemShell is not supported");
                        }
                    } else {
                        Printer.error("Missing MemShell, use [-ms | -memShell] to set");
                    }

                    // 内存马功能
                    if (payloadOptions.get(CliOptions.MemShellFunction) != null) {
                        if (payloadOptions.get(CliOptions.MemShellFunction).toString().equalsIgnoreCase(MemShellFunction.Godzilla)) {
                            javaClassHelper.setMemShellFunction(MemShellFunction.Godzilla);
                        } else if (payloadOptions.get(CliOptions.MemShellFunction).toString().equalsIgnoreCase(MemShellFunction.Runtime)) {
                            javaClassHelper.setMemShellFunction(MemShellFunction.Runtime);
                        }
                    } else {
                        Printer.error("Missing MemShellFunction, use [-msf | -memShellFunction] to set");
                    }
                }
                // RCE 回显
                else if (payloadOptions.get(CliOptions.JavaClassHelperType).toString().equalsIgnoreCase(JavaClassHelperType.RceEcho)) {
                    javaClassHelper.setJavaClassHelperType(JavaClassHelperType.RceEcho);
                } else {
                    Printer.error("The JavaClassHelperType is not supported");
                }
            } else {
                Printer.error("Missing JavaClassHelperType, use [-jht | -javaClassHelperType] to set");
            }
            isJavaClass = true;
        }

        /**
         * 本地字节码加载
         */
        if (payloadOptions.get(CliOptions.Enchant).toString().equalsIgnoreCase(EnchantType.LocalLoad)) {
            sinksHelper.setEnchant(EnchantType.LocalLoad);
            sinksHelper.setJavaClassHelper(javaClassHelper);
            if (!isJavaClass) {
                Printer.error("Missing JavaClass, use [-e | -enchant] to set javaClass");
            }
            if (payloadOptions.get(CliOptions.LoadFunction) != null) {
                if (payloadOptions.get(CliOptions.LoadFunction).toString().equalsIgnoreCase(EnchantType.RHINO)) {
                    sinksHelper.setLoadFunction(EnchantType.RHINO);
                }
            }
        }


        return sinksHelper;
    }


    public static void initPayloadOptions(CommandLine commandLine) {
        if (commandLine.hasOption(CliOptions.Gadget)) {
            payloadOptions.put(CliOptions.Gadget, commandLine.getOptionValue(CliOptions.Gadget));
        }
        if (commandLine.hasOption(CliOptions.Output)) {
            payloadOptions.put(CliOptions.Output, commandLine.getOptionValue(CliOptions.Output));
        }
        if (commandLine.hasOption(CliOptions.SavePath)) {
            payloadOptions.put(CliOptions.SavePath, commandLine.getOptionValue(CliOptions.SavePath));
        }

        if (commandLine.hasOption(CliOptions.Enchant)) {
            payloadOptions.put(CliOptions.Enchant, commandLine.getOptionValue(CliOptions.Enchant));
        }
        if (commandLine.hasOption(CliOptions.ExtendsAbstractTranslet)) {
            payloadOptions.put(CliOptions.ExtendsAbstractTranslet, true);
        }

        if (commandLine.hasOption(CliOptions.Command)) {
            payloadOptions.put(CliOptions.Command, commandLine.getOptionValue(CliOptions.Command));
        }
        if (commandLine.hasOption(CliOptions.OS)) {
            payloadOptions.put(CliOptions.OS, commandLine.getOptionValue(CliOptions.OS));
        }
        if (commandLine.hasOption(CliOptions.Host)) {
            payloadOptions.put(CliOptions.Host, commandLine.getOptionValue(CliOptions.Host));
        }
        if (commandLine.hasOption(CliOptions.Delay)) {
            payloadOptions.put(CliOptions.Delay, commandLine.getOptionValue(CliOptions.Delay));
        }
        if (commandLine.hasOption(CliOptions.DelayTime)) {
            payloadOptions.put(CliOptions.DelayTime, Long.parseLong(commandLine.getOptionValue(CliOptions.DelayTime)));
        }
        if (commandLine.hasOption(CliOptions.ServerFilePath)) {
            payloadOptions.put(CliOptions.ServerFilePath, commandLine.getOptionValue(CliOptions.ServerFilePath));
        }
        if (commandLine.hasOption(CliOptions.LocalFilePath)) {
            payloadOptions.put(CliOptions.LocalFilePath, commandLine.getOptionValue(CliOptions.LocalFilePath));
        }
        if (commandLine.hasOption(CliOptions.FileContent)) {
            payloadOptions.put(CliOptions.FileContent, commandLine.getOptionValue(CliOptions.FileContent));
        }
        if (commandLine.hasOption(CliOptions.URL)) {
            payloadOptions.put(CliOptions.URL, commandLine.getOptionValue(CliOptions.URL));
        }
        if (commandLine.hasOption(CliOptions.RemoteClassName)) {
            payloadOptions.put(CliOptions.RemoteClassName, commandLine.getOptionValue(CliOptions.RemoteClassName));
        }
        if (commandLine.hasOption(CliOptions.Constructor)) {
            payloadOptions.put(CliOptions.Constructor, commandLine.getOptionValue(CliOptions.Constructor));
        }
        if (commandLine.hasOption(CliOptions.LoadFunction)) {
            payloadOptions.put(CliOptions.LoadFunction, commandLine.getOptionValue(CliOptions.LoadFunction));
        }

        if (commandLine.hasOption(CliOptions.JavaClassHelperType)) {
            payloadOptions.put(CliOptions.JavaClassHelperType, commandLine.getOptionValue(CliOptions.JavaClassHelperType));
        }
        if (commandLine.hasOption(CliOptions.Middleware)) {
            payloadOptions.put(CliOptions.Middleware, commandLine.getOptionValue(CliOptions.Middleware));
        }
        if (commandLine.hasOption(CliOptions.MemShell)) {
            payloadOptions.put(CliOptions.MemShell, commandLine.getOptionValue(CliOptions.MemShell));
        }
        if (commandLine.hasOption(CliOptions.MemShellFunction)) {
            payloadOptions.put(CliOptions.MemShellFunction, commandLine.getOptionValue(CliOptions.MemShellFunction));
        }

    }
}
