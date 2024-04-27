package com.ppp;

import com.ppp.chain.commonsbeanutils.CBVersionEnum;
import com.ppp.chain.urldns.URLDNS;
import com.ppp.enums.Output;
import com.ppp.enums.SerializationType;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.GadgetDependency;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;

import static com.ppp.CliOptions.CBVersion;

/**
 * @author Whoopsunix
 * <p>
 * Cli
 */
public class CliScheduler {
    public static Class run(String[] args, SinksHelper sinksHelper) throws Exception {
        Options options = new Options();
        options.addOption(CliOptions.Help.getOpt(), CliOptions.Help.getLongOpt(), false, CliOptions.Help.getDescription());

        options.addOption(CliOptions.Gadget.getOpt(), CliOptions.Gadget.getLongOpt(), true, CliOptions.Gadget.getDescription());
        options.addOption(CliOptions.Show.getOpt(), CliOptions.Show.getLongOpt(), false, CliOptions.Show.getDescription());
        options.addOption(CliOptions.SerializationType.getOpt(), CliOptions.SerializationType.getLongOpt(), true, CliOptions.SerializationType.getDescription());
        options.addOption(CliOptions.Output.getOpt(), CliOptions.Output.getLongOpt(), true, CliOptions.Output.getDescription());
        options.addOption(CliOptions.SavePath.getOpt(), CliOptions.SavePath.getLongOpt(), true, CliOptions.SavePath.getDescription());
        options.addOption(CliOptions.ClosePrinter.getOpt(), CliOptions.ClosePrinter.getLongOpt(), false, CliOptions.ClosePrinter.getDescription());

        options.addOption(CliOptions.Enchant.getOpt(), CliOptions.Enchant.getLongOpt(), true, CliOptions.Enchant.getDescription());

        // 附加参数
        options.addOption(CliOptions.Command.getOpt(), CliOptions.Command.getLongOpt(), true, CliOptions.Command.getDescription());
        options.addOption(CliOptions.CommandType.getOpt(), CliOptions.CommandType.getLongOpt(), true, CliOptions.CommandType.getDescription());
        options.addOption(CliOptions.Split.getOpt(), CliOptions.Split.getLongOpt(), false, CliOptions.Split.getDescription());
        options.addOption(CliOptions.Code.getOpt(), CliOptions.Code.getLongOpt(), true, CliOptions.Code.getDescription());
        options.addOption(CliOptions.CodeFile.getOpt(), CliOptions.CodeFile.getLongOpt(), true, CliOptions.CodeFile.getDescription());
        options.addOption(CliOptions.DelayTime.getOpt(), CliOptions.DelayTime.getLongOpt(), true, CliOptions.DelayTime.getDescription());
        options.addOption(CliOptions.Host.getOpt(), CliOptions.Host.getLongOpt(), true, CliOptions.Host.getDescription());
        options.addOption(CliOptions.ServerFilePath.getOpt(), CliOptions.ServerFilePath.getLongOpt(), true, CliOptions.ServerFilePath.getDescription());
        options.addOption(CliOptions.LocalFilePath.getOpt(), CliOptions.LocalFilePath.getLongOpt(), true, CliOptions.LocalFilePath.getDescription());
        options.addOption(CliOptions.FileContent.getOpt(), CliOptions.FileContent.getLongOpt(), true, CliOptions.FileContent.getDescription());
        options.addOption(CliOptions.Append.getOpt(), CliOptions.Append.getLongOpt(), false, CliOptions.Append.getDescription());
        options.addOption(CliOptions.PartSize.getOpt(), CliOptions.PartSize.getLongOpt(), true, CliOptions.PartSize.getDescription());

        options.addOption(CliOptions.LoadFunction.getOpt(), CliOptions.LoadFunction.getLongOpt(), true, CliOptions.LoadFunction.getDescription());
        options.addOption(CliOptions.URL.getOpt(), CliOptions.URL.getLongOpt(), true, CliOptions.URL.getDescription());
        options.addOption(CliOptions.ClassName.getOpt(), CliOptions.ClassName.getLongOpt(), true, CliOptions.ClassName.getDescription());
        options.addOption(CliOptions.Constructor.getOpt(), CliOptions.Constructor.getLongOpt(), true, CliOptions.Constructor.getDescription());

        // JavaClass
        options.addOption(CliOptions.ExtendsAbstractTranslet.getOpt(), CliOptions.ExtendsAbstractTranslet.getLongOpt(), false, CliOptions.ExtendsAbstractTranslet.getDescription());
        options.addOption(CliOptions.JavaClassHelperType.getOpt(), CliOptions.JavaClassHelperType.getLongOpt(), true, CliOptions.JavaClassHelperType.getDescription());
        options.addOption(CliOptions.JavaClassEnhance.getOpt(), CliOptions.JavaClassEnhance.getLongOpt(), true, CliOptions.JavaClassEnhance.getDescription());
        options.addOption(CliOptions.JavaClassFilePath.getOpt(), CliOptions.JavaClassFilePath.getLongOpt(), true, CliOptions.JavaClassFilePath.getDescription());
        options.addOption(CliOptions.JavaClassType.getOpt(), CliOptions.JavaClassType.getLongOpt(), true, CliOptions.JavaClassType.getDescription());
        options.addOption(CliOptions.Middleware.getOpt(), CliOptions.Middleware.getLongOpt(), true, CliOptions.Middleware.getDescription());
        options.addOption(CliOptions.MemShell.getOpt(), CliOptions.MemShell.getLongOpt(), true, CliOptions.MemShell.getDescription());
        options.addOption(CliOptions.MemShellFunction.getOpt(), CliOptions.MemShellFunction.getLongOpt(), true, CliOptions.MemShellFunction.getDescription());
        // JavaClass 自定义字段
        options.addOption(CliOptions.FieldNAME.getOpt(), CliOptions.FieldNAME.getLongOpt(), true, CliOptions.FieldNAME.getDescription());
        options.addOption(CliOptions.FieldHEADER.getOpt(), CliOptions.FieldHEADER.getLongOpt(), true, CliOptions.FieldHEADER.getDescription());
//        options.addOption(CliOptions.FieldPARAM.getOpt(), CliOptions.FieldPARAM.getLongOpt(), true, CliOptions.FieldPARAM.getDescription());
        options.addOption(CliOptions.FieldPATH.getOpt(), CliOptions.FieldPATH.getLongOpt(), true, CliOptions.FieldPATH.getDescription());
        options.addOption(CliOptions.Fieldkey.getOpt(), CliOptions.Fieldkey.getLongOpt(), true, CliOptions.Fieldkey.getDescription());
        options.addOption(CliOptions.Fieldpass.getOpt(), CliOptions.Fieldpass.getLongOpt(), true, CliOptions.Fieldpass.getDescription());
        options.addOption(CliOptions.FieldLockHeaderKey.getOpt(), CliOptions.FieldLockHeaderKey.getLongOpt(), true, CliOptions.FieldLockHeaderKey.getDescription());
        options.addOption(CliOptions.FieldLockHeaderValue.getOpt(), CliOptions.FieldLockHeaderValue.getLongOpt(), true, CliOptions.FieldLockHeaderValue.getDescription());

        // DNSLOG
        options.addOption(CliOptions.DNSProducts.getOpt(), CliOptions.DNSProducts.getLongOpt(), true, CliOptions.DNSProducts.getDescription());
        options.addOption(CliOptions.DNSSubdomain.getOpt(), CliOptions.DNSSubdomain.getLongOpt(), true, CliOptions.DNSSubdomain.getDescription());

        options.addOption(CliOptions.WrapSerialization.getOpt(), CliOptions.WrapSerialization.getLongOpt(), false, CliOptions.WrapSerialization.getDescription());
        options.addOption(CBVersion.getOpt(), CBVersion.getLongOpt(), true, CBVersion.getDescription());
        options.addOption(CliOptions.GadgetDependency.getOpt(), CliOptions.GadgetDependency.getLongOpt(), true, CliOptions.GadgetDependency.getDescription());


        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption(CliOptions.Help.getLongOpt())) {
            Scheduler.PPPYSO();
            HelpFormatter helpFormatter = new HelpFormatter();
//            helpFormatter.printHelp("java -jar PPPYSO-jar-with-dependencies.jar", options, true);
            helpFormatter.printHelp("java -jar PPPYSO-jar-with-dependencies.jar", options, false);
        }

        String Gadget = commandLine.getOptionValue(CliOptions.Gadget.getLongOpt());
        String serializationType = commandLine.getOptionValue(CliOptions.SerializationType.getLongOpt());
        String output = commandLine.getOptionValue(CliOptions.Output.getLongOpt());
        Boolean closePrinter = commandLine.hasOption(CliOptions.ClosePrinter.getLongOpt());
        String savePath = commandLine.getOptionValue(CliOptions.SavePath.getLongOpt());


        if (serializationType != null) {
            sinksHelper.setSerializationType(SerializationType.getSerializationType(serializationType));
        }

        if (output != null) {
            sinksHelper.setOutput(Output.splitOutput(output));
        }
        if (closePrinter) {
            Scheduler.closePrint();
        }
        if (savePath != null) {
            sinksHelper.setSave(true);
            sinksHelper.setSavePath(savePath);
        }

        Class<? extends ObjectPayload> gadgetClass = SinkScheduler.getGadgetClass(Gadget);
        if (Gadget != null) {
            if (gadgetClass != null) {
                String[] sinks = (String[]) Reflections.invokeMethod(gadgetClass.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
                sinksHelper.setSink(sinks[0]);

                if (gadgetClass.equals(URLDNS.class)) {
                    URLDNSGadget(commandLine, sinksHelper);
                } else {
                    commonGadget(commandLine, sinksHelper, gadgetClass);
                }
            } else {
                Printer.error(String.format("No such gadget: %s", Gadget));
            }
        } else {
            sinksHelper.setEnchant(EnchantType.JavaClass);
            commonGadget(commandLine, sinksHelper, gadgetClass);
        }

        return gadgetClass;
    }

    public static void URLDNSGadget(CommandLine commandLine, SinksHelper sinksHelper) throws Exception {
        Map helperMap = modifyCliArgs(commandLine);
        Scheduler.enchantURLDNS(sinksHelper, helperMap);
    }

    public static void commonGadget(CommandLine commandLine, SinksHelper sinksHelper, Class<? extends ObjectPayload> gadgetClass) throws Exception {
        Boolean wrapSerialization = commandLine.hasOption(CliOptions.WrapSerialization.getLongOpt());
        String CBVersion = commandLine.getOptionValue(CliOptions.CBVersion.getLongOpt());
        String enchant = commandLine.getOptionValue(CliOptions.Enchant.getLongOpt());
        Boolean extendsAbstractTranslet = commandLine.hasOption(CliOptions.ExtendsAbstractTranslet.getLongOpt());
        String loadFunction = commandLine.getOptionValue(CliOptions.LoadFunction.getLongOpt());
        String gadgetDependency = commandLine.getOptionValue(CliOptions.GadgetDependency.getLongOpt());


        Map helperMap = modifyCliArgs(commandLine);
        if (commandLine.hasOption(CliOptions.Show.getLongOpt())) {
            Scheduler.PPPYSO();
            SinkScheduler.showGadgetClassEnhances(gadgetClass);
        }

        if (wrapSerialization != null && wrapSerialization) {
            sinksHelper.setWrapSerialization(EnchantEnums.SignedObject);
        }
        if (CBVersion != null) {
            sinksHelper.setCbVersion(CBVersionEnum.getCBVersion(CBVersion));
        }
        if (extendsAbstractTranslet) {
            JavaClassHelper javaClassHelper = sinksHelper.getJavaClassHelper();
            javaClassHelper.setExtendsAbstractTranslet(true);
        }
        if (loadFunction != null) {
            sinksHelper.setLoadFunction(EnchantEnums.getEnchantEnums(loadFunction));
        }
        if (gadgetDependency != null) {
            sinksHelper.setGadgetDependency(GadgetDependency.getGadgetDependency(gadgetDependency));
        }


        if (enchant == null) {
            sinksHelper.setEnchant(EnchantType.DEFAULT);
            String command = (String) helperMap.get(CliOptions.Command.getLongOpt());
            sinksHelper.setCommand(command);
        } else if (enchant.equalsIgnoreCase(EnchantType.Command)) {
            Scheduler.enchantCommand(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.Delay)) {
            Scheduler.enchantDelay(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.Socket)) {
            Scheduler.enchantSocket(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.FileWrite)) {
            Scheduler.enchantFileWrite(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.JavaClass)) {
            Scheduler.enchantJavaClass(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.RemoteLoad)) {
            Scheduler.enchantRemoteLoad(sinksHelper, helperMap);
        } else {
            Printer.error("No such enchant");
        }
    }

    public static Map modifyCliArgs(CommandLine commandLine) {
        Map clihelperMap = new HashMap();

        for (CliOptions cliOption : CliOptions.values()) {
            Object optionValue = commandLine.getOptionValue(cliOption.getLongOpt());
            // 处理 bool 值
            if (optionValue == null && commandLine.hasOption(cliOption.getLongOpt()))
                optionValue = true;
            clihelperMap.put(cliOption.getLongOpt(), optionValue);
        }
        return clihelperMap;
    }

}
