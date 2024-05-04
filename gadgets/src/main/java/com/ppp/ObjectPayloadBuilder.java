package com.ppp;


import com.ppp.enums.Output;
import com.ppp.enums.SerializationType;
import com.ppp.mix.UTF8BytesMix;
import com.ppp.sinks.SinksHelper;
import com.ppp.utils.Serializer;
import com.ppp.utils.maker.CryptoUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

/**
 * @author Whoopsunix
 */
public class ObjectPayloadBuilder {

    public static Object builder(Class<? extends ObjectPayload> cls, SinksHelper sinksHelper) throws Exception {
        // 生成 Gadget
        ObjectPayload payload = cls.newInstance();
        Printer.blueInfo("Gadget: " + payload.getClass().getSimpleName());
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
        SerializationType serialization = sinksHelper.getSerializationType();


        if (gadget instanceof byte[]) {
            bytes = (byte[]) gadget;
//            Printer.yellowInfo("Gadget result is byte[], output change is not supported.");
            switch (serialization) {
                case UTF8Mix:
                    bytes = new UTF8BytesMix(bytes).builder();
                    break;
            }

        } else {
            switch (serialization) {
                default:
                case Default:
                    bytes = Serializer.serialize(gadget);
                    break;
                case XStream:
                    bytes = Serializer.serializeXStream(gadget).getBytes();
                    break;
                case HexAscii:
                    bytes = Serializer.serializeHexAscii(gadget).getBytes();
                    break;
                case UTF8Mix:
                    bytes = new UTF8BytesMix(Serializer.serialize(gadget)).builder();
                    break;
            }
        }
        return bytes;
    }

    public static void save(byte[] bytes, SinksHelper sinksHelper) throws Exception {
        Output[] output = sinksHelper.getOutput();
        boolean loop = sinksHelper.isLoop();

        for (Output o : output) {
            switch (o) {
                default:
                case Default:
                    bytes = defaultSave(bytes);
                    break;
                case GZIP:
                    bytes = gzipSave(bytes);
                    break;
                case Base64:
                    bytes = base64Save(bytes);
                    break;
            }
        }

        // 循环生成的话太长了所以不输出
        if (!loop) {
            Printer.blueInfo("Output: " + Arrays.toString(output));
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(bytes);
            Printer.result(byteArrayOutputStream);
        }

        // 保存文件
        if (sinksHelper.isSave()) {
            FileOutputStream fileOutputStream = new FileOutputStream(sinksHelper.getSavePath());
            fileOutputStream.write(bytes);
            fileOutputStream.close();
            Printer.yellowInfo("Gadget length: " + bytes.length);
            Printer.yellowInfo("Gadget save in " + sinksHelper.getSavePath());
        }
    }

    public static byte[] defaultSave(byte[] bytes) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(bytes);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] gzipSave(byte[] bytes) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream1);
        gzipOutputStream.write(bytes);
        gzipOutputStream.close();
        byteArrayOutputStream1.close();
        return byteArrayOutputStream1.toByteArray();
    }

    public static byte[] base64Save(byte[] bytes) throws Exception {
        String s = CryptoUtils.base64encoder(bytes);
        return s.getBytes();
    }

}
