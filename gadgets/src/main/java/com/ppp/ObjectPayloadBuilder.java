package com.ppp;


import com.ppp.enums.Output;
import com.ppp.enums.SerializationType;
import com.ppp.sinks.SinksHelper;
import com.ppp.utils.Serializer;
import com.ppp.utils.maker.CryptoUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;

/**
 * @author Whoopsunix
 */
public class ObjectPayloadBuilder {

    public static Object builder(Class<? extends ObjectPayload> cls, SinksHelper sinksHelper) throws Exception {
        // 生成 Gadget
        ObjectPayload payload = cls.newInstance();
        Object gadget = payload.getObject(sinksHelper);

        byte[] bytes = original(gadget, sinksHelper);
        save(bytes, sinksHelper);

        // todo 似乎没用
        return bytes;
    }

    /**
     * 原始序列化
     *
     * @param gadget
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    public static byte[] original(Object gadget, SinksHelper sinksHelper) throws Exception {
        byte[] bytes = new byte[0];
        SerializationType serialization = sinksHelper.getSerialization();


        if (gadget instanceof byte[]) {
            bytes = (byte[]) gadget;
            Printer.yellowInfo("Gadget result is byte[], output change is not supported.");
        } else {
            switch (serialization) {
                default:
                case Default:
                    bytes = Serializer.serialize(gadget);
                    break;
                case GZIP:
                    bytes = Serializer.serializeGZip(gadget);
                    break;
                case XStream:
                    bytes = Serializer.serializeXStream(gadget).getBytes();
                    break;
                case HexAscii:
                    bytes = Serializer.serializeHexAscii(gadget).getBytes();
                    break;
                case UTF8Mix:
                    bytes = Serializer.serializeUTF8(gadget);
                    break;
            }
        }
        return bytes;
    }

    public static void save(byte[] bytes, SinksHelper sinksHelper) throws Exception {
        Output output = sinksHelper.getOutput();
        switch (output){
            default:
            case Default:
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(bytes);
                System.out.println(byteArrayOutputStream);
                break;
            case Base64:
                String s = CryptoUtils.base64encoder(bytes);
                System.out.println(s);
        }

        // 保存文件
        if (sinksHelper.isSave()) {
            FileOutputStream fileOutputStream = new FileOutputStream(sinksHelper.getSavePath());
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        }
    }
}
