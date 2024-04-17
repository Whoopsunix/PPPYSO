package com.ppp;

import com.ppp.enums.Output;
import com.ppp.enums.SerializationType;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;

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
        options.addOption(CliOptions.SerializationType.getOpt(), CliOptions.SerializationType.getLongOpt(), true, CliOptions.SerializationType.getDescription());
        options.addOption(CliOptions.Output.getOpt(), CliOptions.Output.getLongOpt(), true, CliOptions.Output.getDescription());
        options.addOption(CliOptions.SavePath.getOpt(), CliOptions.SavePath.getLongOpt(), true, CliOptions.SavePath.getDescription());
        options.addOption(CliOptions.ClosePrinter.getOpt(), CliOptions.ClosePrinter.getLongOpt(), false, CliOptions.ClosePrinter.getDescription());

        options.addOption(CliOptions.Enchant.getOpt(), CliOptions.Enchant.getLongOpt(), true, CliOptions.Enchant.getDescription());

        // 附加参数
        options.addOption(CliOptions.Command.getOpt(), CliOptions.Command.getLongOpt(), true, CliOptions.Command.getDescription());
        options.addOption(CliOptions.OS.getOpt(), CliOptions.OS.getLongOpt(), true, CliOptions.OS.getDescription());
        options.addOption(CliOptions.Code.getOpt(), CliOptions.Code.getLongOpt(), true, CliOptions.Code.getDescription());
        options.addOption(CliOptions.CodeFile.getOpt(), CliOptions.CodeFile.getLongOpt(), true, CliOptions.CodeFile.getDescription());
        options.addOption(CliOptions.DelayTime.getOpt(), CliOptions.DelayTime.getLongOpt(), true, CliOptions.DelayTime.getDescription());
        options.addOption(CliOptions.Host.getOpt(), CliOptions.Host.getLongOpt(), true, CliOptions.Host.getDescription());
        options.addOption(CliOptions.ServerFilePath.getOpt(), CliOptions.ServerFilePath.getLongOpt(), true, CliOptions.ServerFilePath.getDescription());
        options.addOption(CliOptions.LocalFilePath.getOpt(), CliOptions.LocalFilePath.getLongOpt(), true, CliOptions.LocalFilePath.getDescription());
        options.addOption(CliOptions.FileContent.getOpt(), CliOptions.FileContent.getLongOpt(), true, CliOptions.FileContent.getDescription());
        options.addOption(CliOptions.LoadFunction.getOpt(), CliOptions.LoadFunction.getLongOpt(), true, CliOptions.LoadFunction.getDescription());
        options.addOption(CliOptions.URL.getOpt(), CliOptions.URL.getLongOpt(), true, CliOptions.URL.getDescription());
        options.addOption(CliOptions.RemoteClassName.getOpt(), CliOptions.RemoteClassName.getLongOpt(), true, CliOptions.RemoteClassName.getDescription());
        options.addOption(CliOptions.Constructor.getOpt(), CliOptions.Constructor.getLongOpt(), true, CliOptions.Constructor.getDescription());

        // JavaClass
        options.addOption(CliOptions.ExtendsAbstractTranslet.getOpt(), CliOptions.ExtendsAbstractTranslet.getLongOpt(), false, CliOptions.ExtendsAbstractTranslet.getDescription());
        options.addOption(CliOptions.JavaClassHelperType.getOpt(), CliOptions.JavaClassHelperType.getLongOpt(), true, CliOptions.JavaClassHelperType.getDescription());
        options.addOption(CliOptions.JavaClassFilePath.getOpt(), CliOptions.JavaClassFilePath.getLongOpt(), true, CliOptions.JavaClassFilePath.getDescription());
        options.addOption(CliOptions.JavaClassType.getOpt(), CliOptions.JavaClassType.getLongOpt(), true, CliOptions.JavaClassType.getDescription());
        options.addOption(CliOptions.Middleware.getOpt(), CliOptions.Middleware.getLongOpt(), true, CliOptions.Middleware.getDescription());
        options.addOption(CliOptions.MemShell.getOpt(), CliOptions.MemShell.getLongOpt(), true, CliOptions.MemShell.getDescription());
        options.addOption(CliOptions.MemShellFunction.getOpt(), CliOptions.MemShellFunction.getLongOpt(), true, CliOptions.MemShellFunction.getDescription());
        // JavaClass 自定义字段
        options.addOption(CliOptions.FieldNAME.getOpt(), CliOptions.FieldNAME.getLongOpt(), true, CliOptions.FieldNAME.getDescription());
        options.addOption(CliOptions.FieldHEADER.getOpt(), CliOptions.FieldHEADER.getLongOpt(), true, CliOptions.FieldHEADER.getDescription());
        options.addOption(CliOptions.FieldPARAM.getOpt(), CliOptions.FieldPARAM.getLongOpt(), true, CliOptions.FieldPARAM.getDescription());
        options.addOption(CliOptions.FieldPATH.getOpt(), CliOptions.FieldPATH.getLongOpt(), true, CliOptions.FieldPATH.getDescription());
        options.addOption(CliOptions.Fieldkey.getOpt(), CliOptions.Fieldkey.getLongOpt(), true, CliOptions.Fieldkey.getDescription());
        options.addOption(CliOptions.Fieldpass.getOpt(), CliOptions.Fieldpass.getLongOpt(), true, CliOptions.Fieldpass.getDescription());
        options.addOption(CliOptions.FieldLockHeaderKey.getOpt(), CliOptions.FieldLockHeaderKey.getLongOpt(), true, CliOptions.FieldLockHeaderKey.getDescription());
        options.addOption(CliOptions.FieldLockHeaderValue.getOpt(), CliOptions.FieldLockHeaderValue.getLongOpt(), true, CliOptions.FieldLockHeaderValue.getDescription());

        // DNSLOG
        options.addOption(CliOptions.DNSHost.getOpt(), CliOptions.DNSHost.getLongOpt(), true, CliOptions.DNSHost.getDescription());
        options.addOption(CliOptions.DNSProducts.getOpt(), CliOptions.DNSProducts.getLongOpt(), true, CliOptions.DNSProducts.getDescription());
        options.addOption(CliOptions.DNSClassName.getOpt(), CliOptions.DNSClassName.getLongOpt(), true, CliOptions.DNSClassName.getDescription());
        options.addOption(CliOptions.DNSSubdomain.getOpt(), CliOptions.DNSSubdomain.getLongOpt(), true, CliOptions.DNSSubdomain.getDescription());

        options.addOption(CliOptions.WrapSerialization.getOpt(), CliOptions.WrapSerialization.getLongOpt(), false, CliOptions.WrapSerialization.getDescription());


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
        Boolean save = commandLine.hasOption(CliOptions.SavePath.getLongOpt());
        Boolean closePrinter = commandLine.hasOption(CliOptions.ClosePrinter.getLongOpt());
        String savePath = commandLine.getOptionValue(CliOptions.SavePath.getLongOpt());
        Boolean wrapSerialization = commandLine.hasOption(CliOptions.WrapSerialization.getLongOpt());
        String CBVersion = commandLine.getOptionValue(CliOptions.CBVersion.getLongOpt());
        String enchant = commandLine.getOptionValue(CliOptions.Enchant.getLongOpt());
        Map helperMap = modifyCliArgs(commandLine);

        Class<? extends ObjectPayload> gadgetClass = SinkScheduler.getGadgetClass(Gadget);
        String[] sinks = (String[]) Reflections.invokeMethod(gadgetClass.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
        sinksHelper.setSink(sinks[0]);

        if (serializationType != null) {
            sinksHelper.setSerializationType(SerializationType.getSerializationType(serializationType));
        }

        if (output != null) {
            sinksHelper.setOutput(Output.getOutput(output));
        }
        if (closePrinter) {
            Scheduler.closePrint();
        }
        if (save != null) {
            sinksHelper.setSave(save);
        }
        if (savePath != null) {
            sinksHelper.setSave(true);
            sinksHelper.setSavePath(savePath);
        }
        if (wrapSerialization != null && wrapSerialization) {
            sinksHelper.setWrapSerialization(EnchantEnums.SignedObject);
        }
        if (CBVersion != null) {
            sinksHelper.setCBVersion(CBVersion);
        }

        if (enchant == null) {
            sinksHelper.setEnchant(EnchantType.DEFAULT);
        } else if (enchant.equalsIgnoreCase(EnchantType.Command)) {
            Scheduler.enchantCommand(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.Delay)) {
            Scheduler.enchantDelay(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.Socket)) {
            Scheduler.enchantSocket(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.FileWrite)) {
            Scheduler.enchantFileWrite(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.LocalLoad)) {
            Scheduler.enchantJavaClass(sinksHelper, helperMap);
            Scheduler.enchantLocalLoad(sinksHelper, helperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.JavaClass)) {
            Scheduler.enchantJavaClass(sinksHelper, helperMap);
        }


        return gadgetClass;
    }

    public static Map modifyCliArgs(CommandLine commandLine){
        Map clihelperMap = new HashMap();

        for (CliOptions cliOption : CliOptions.values()) {
            clihelperMap.put(cliOption.getLongOpt(), commandLine.getOptionValue(cliOption.getLongOpt()));
        }
        return clihelperMap;
    }

}
