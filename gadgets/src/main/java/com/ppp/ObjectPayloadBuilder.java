package com.ppp;

import com.ppp.annotation.Save;
import com.ppp.sinks.SinksHelper;
import com.ppp.utils.Serializer;

import java.io.PrintStream;

/**
 * @author Whoopsunix
 */
public class ObjectPayloadBuilder {

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
