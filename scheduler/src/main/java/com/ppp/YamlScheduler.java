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
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.Map;

/**
 * @author Whoopsunix
 * <p>
 * Yaml 加载
 */
public class YamlScheduler {
    public static void main(String[] args) throws Exception {
        String configPath = "/Users/ppp/Documents/pppRepository/github_file/PPPYSO/scheduler/src/main/resources/PPPConfig.yml";
        run(configPath, new SinksHelper());
    }

    public static Class run(String configPath, SinksHelper sinksHelper) throws Exception {
        Map<String, Object> config = loadYamlConfig(configPath);
        Map PPPYSO = (Map) config.get("PPPYSO");
        String Gadget = (String) PPPYSO.get("Gadget");
        String output = (String) PPPYSO.get("output");
        Boolean closePrinter = (Boolean) PPPYSO.get("closePrinter");
        Boolean save = (Boolean) PPPYSO.get("save");
        String savePath = (String) PPPYSO.get("savePath");

        if (output != null) {
            sinksHelper.setOutput(Output.splitOutput(output));
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


        Class<? extends ObjectPayload> gadgetClass = SinkScheduler.getGadgetClass(Gadget);
        if (Gadget != null) {
            if (gadgetClass != null) {
                String[] sinks = (String[]) Reflections.invokeMethod(gadgetClass.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
                sinksHelper.setSink(sinks[0]);

                if (gadgetClass.equals(URLDNS.class)) {
                    URLDNSGadget(PPPYSO, sinksHelper);
                } else {
                    commonGadget(PPPYSO, sinksHelper);
                }
            } else {
                Printer.error(String.format("No such gadget: %s", Gadget));
            }
        } else {
            sinksHelper.setEnchant(EnchantType.JavaClass);
            commonGadget(PPPYSO, sinksHelper);
        }


        return gadgetClass;
    }


    public static Map<String, Object> loadYamlConfig(String filePath) {
        try {
            Yaml yaml = new Yaml();
            FileInputStream fileInputStream = new FileInputStream(filePath);
            Map<String, Object> config = yaml.load(fileInputStream);

            return config;
        } catch (Exception e) {
            Printer.error("Config yaml file not find");
        }
        return null;
    }

    public static void URLDNSGadget(Map PPPYSO, SinksHelper sinksHelper) throws Exception {
        Map dnsHelperMap = (Map) PPPYSO.get("DNSHelper");

        Scheduler.enchantURLDNS(sinksHelper, dnsHelperMap);
    }

    public static void commonGadget(Map PPPYSO, SinksHelper sinksHelper) throws Exception {
        String serializationType = (String) PPPYSO.get(CliOptions.SerializationType.getLongOpt());
        Boolean wrapSerialization = (Boolean) PPPYSO.get(CliOptions.WrapSerialization.getLongOpt());
        String CBVersion = (String) PPPYSO.get(CliOptions.CBVersion.getLongOpt());
        Map sinksHelperMap = (Map) PPPYSO.get("SinksHelper");
        String enchant = (String) sinksHelperMap.get("enchant");
        Map javaClassHelperMap = (Map) PPPYSO.get("JavaClassHelper");
        String gadgetDependency = (String) PPPYSO.get(CliOptions.GadgetDependency.getLongOpt());

        if (serializationType != null) {
            sinksHelper.setSerializationType(SerializationType.getSerializationType(serializationType));
        }
        if (wrapSerialization != null && wrapSerialization) {
            sinksHelper.setWrapSerialization(EnchantEnums.SignedObject);
        }
        if (CBVersion != null) {
            sinksHelper.setCbVersion(CBVersionEnum.getCBVersion(CBVersion));
        }
        if (gadgetDependency != null) {
            sinksHelper.setGadgetDependency(GadgetDependency.getGadgetDependency(gadgetDependency));
        }

        if (enchant == null) {
            sinksHelper.setEnchant(EnchantType.DEFAULT);
        } else if (enchant.equalsIgnoreCase(EnchantType.Command)) {
            Scheduler.enchantCommand(sinksHelper, sinksHelperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.Delay)) {
            Scheduler.enchantDelay(sinksHelper, sinksHelperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.Socket)) {
            Scheduler.enchantSocket(sinksHelper, sinksHelperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.FileWrite)) {
            Scheduler.enchantFileWrite(sinksHelper, sinksHelperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.JavaClass)) {
            Scheduler.enchantJavaClass(sinksHelper, javaClassHelperMap);
        } else if (enchant.equalsIgnoreCase(EnchantType.RemoteLoad)) {
            Scheduler.enchantRemoteLoad(sinksHelper, sinksHelperMap);
        }
    }
}
