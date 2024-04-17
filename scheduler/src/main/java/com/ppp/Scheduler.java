package com.ppp;

import com.ppp.annotation.*;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.utils.maker.ClassUtils;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author Whoopsunix
 */
public class Scheduler {
    private static String gadgetPackageName = "com.ppp.chain";
    private static String userDir = System.getProperty("user.dir") + "/PPPConfig.yml";

    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        Class cls = null;
        if (args.length == 0) {
            PPPYSOexit();
        } else if (args.length == 1 && !args[0].equalsIgnoreCase("-h") && !args[0].equalsIgnoreCase("-help")) {
            String configPath = args[0];
            if (!new File(configPath).exists() && new File(userDir).exists()) {
                configPath = userDir;
            } else {
                PPPYSOexit();
            }

            cls = YamlScheduler.run(configPath, sinksHelper);
        } else {
            cls = CliScheduler.run(args, sinksHelper);
        }

        Object gadget = ObjectPayloadBuilder.builder(cls, sinksHelper);
    }

    public static void PPPYSOexit() throws Exception {
        PPPYSO();
        if (!new File(userDir).exists()) {
            generateDefaultConfigYaml();
        }
        SinkScheduler.showGadget();
        System.exit(0);
    }

    public static void PPPYSO() {
        String banner = " __ ___ ___ __  __ __  _  \n" +
                "| o \\ o \\ o \\\\ V // _|/ \\ \n" +
                "|  _/  _/  _/ \\ / \\_ ( o )\n" +
                "|_| |_| |_|   |_| |__/\\_/ \n" +
                "    1.1.0   By. Whoopsunix \n";
        Printer.print(banner);

    }

    public static void generateDefaultConfigYaml() throws Exception {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("PPPConfig.yml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));

        // 写入文件
        FileWriter fileWriter = new FileWriter(userDir);
        String line;
        while ((line = reader.readLine()) != null) {
            fileWriter.write(line + "\n");
        }
        // 关闭 FileWriter 和 BufferedReader
        fileWriter.close();
        reader.close();
        resourceAsStream.close();
        Printer.warn("Config file not found, Generate a default configuration file PPPConfig.yml in the current directory");
    }

    public static void closePrint() {
        System.setProperty(Printer.PRINT_ENABLED_PROPERTY, String.valueOf(false));
    }

    /**
     * 命令执行增强
     */
    public static void enchantCommand(SinksHelper sinksHelper, Map SinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.Command);
        String command = (String) SinksHelperMap.get("command");
        String commandType = (String) SinksHelperMap.get("commandType");
        String os = (String) SinksHelperMap.get("os");
        String code = (String) SinksHelperMap.get("code");
        String codeFile = (String) SinksHelperMap.get("codeFile");
        if (command == null) {
            Printer.error("Command is null");
        }
        sinksHelper.setCommand(command);
        if (commandType != null) {
            sinksHelper.setCommandType(EnchantEnums.getEnchantEnums(commandType));
        } else {
            sinksHelper.setCommandType(EnchantEnums.Runtime);
            Printer.log("Use default command type: Runtime");
        }
        if (os != null) {
            sinksHelper.setOs(EnchantEnums.getEnchantEnums(os));
        }
        if (code != null) {
            sinksHelper.setCode(code);
        }
        if (codeFile != null) {
            sinksHelper.setCodeFile(codeFile);
        }
    }

    /**
     * 延时增强
     */
    public static void enchantDelay(SinksHelper sinksHelper, Map SinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.Delay);
        Long delayTime = (Long) SinksHelperMap.get("delayTime");
        if (delayTime == null) {
            Printer.error("Delay time is null");
        }
        sinksHelper.setDelayTime(delayTime);

    }

    /**
     * Socket 增强
     */
    public static void enchantSocket(SinksHelper sinksHelper, Map SinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.Socket);
        String host = (String) SinksHelperMap.get("host");
        if (host == null) {
            Printer.error("Host is null");
        }
        sinksHelper.setHost(host);
    }

    /**
     * 文件写入增强
     */
    public static void enchantFileWrite(SinksHelper sinksHelper, Map SinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.FileWrite);
        String serverFilePath = (String) SinksHelperMap.get("serverFilePath");
        String localFilePath = (String) SinksHelperMap.get("localFilePath");
        String fileContent = (String) SinksHelperMap.get("fileContent");
        if (serverFilePath == null) {
            Printer.error("Server file path is null");
        }
        sinksHelper.setServerFilePath(serverFilePath);
        if (localFilePath != null) {
            sinksHelper.setLocalFilePath(localFilePath);
        } else if (fileContent != null) {
            sinksHelper.setFileContent(fileContent);
        } else {
            Printer.error("Please set local file path or file content");
        }
    }

    /**
     * 本地加载增强
     */
    public static void enchantLocalLoad(SinksHelper sinksHelper, Map SinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.LocalLoad);
        String loadFunction = (String) SinksHelperMap.get("loadFunction");
        if (loadFunction != null) {
            sinksHelper.setLoadFunction(EnchantEnums.getEnchantEnums(loadFunction));
        } else {
            sinksHelper.setLoadFunction(EnchantEnums.ScriptEngine);
            Printer.log("Use default load function: ScriptEngine");
        }
    }

    /**
     * JavaClass 增强
     *
     * @param sinksHelper
     * @param javaClassHelperMap
     */
    public static void enchantJavaClass(SinksHelper sinksHelper, Map javaClassHelperMap) {
        sinksHelper.setEnchant(EnchantType.JavaClass);

        JavaClassHelper javaClassHelper = sinksHelper.getJavaClassHelper();
        javaClassHelper.setJavaClassHelperType(JavaClassHelperType.Utils.getJavaClassHelperType((String) javaClassHelperMap.get("javaClassHelperType")));
        javaClassHelper.setJavaClassType(JavaClassType.Utils.getJavaClassType((String) javaClassHelperMap.get("javaClassType")));
        javaClassHelper.setMiddleware(Middleware.Utils.getMiddleware((String) javaClassHelperMap.get("middleware")));
        javaClassHelper.setMemShell(MemShell.Utils.getMemShell((String) javaClassHelperMap.get("memShell")));
        javaClassHelper.setMemShellFunction(MemShellFunction.Utils.getMemShellFunction((String) javaClassHelperMap.get("memShellFunction")));
    }


}
