package com.ppp.utils;

import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.zip.GZIPOutputStream;

/**
 * 序列化工具类
 */
public class Serializer implements Callable<byte[]> {
    private final Object object;

    public Serializer(Object object) {
        this.object = object;
    }

    public byte[] call() throws Exception {
        return serialize(object);
    }

    public static byte[] serialize(final Object obj) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        serialize(obj, out);
        return out.toByteArray();
    }

    public static void serialize(final Object obj, final OutputStream out) throws IOException {
        final ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject(obj);
    }

    public static void serialize(Object obj, String fileName) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(obj);
    }

    /**
     * Base64 序列化结果
     */
    public static String serializeBase64(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        String base64str = new sun.misc.BASE64Encoder().encode(baos.toByteArray());
        base64str = base64str.replaceAll("\n|\r", "");
        return base64str;
    }

    public static void serializeGZip(Object obj, final OutputStream out) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(out));
        oos.writeObject(obj);
        oos.close();
    }

    public static void serializeGZip(Object obj, String fileName) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * Base64 GZip
     */
    public static String serializeBase64GZip(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baos));
        oos.writeObject(obj);
        oos.close();
        String base64str = new sun.misc.BASE64Encoder().encode(baos.toByteArray());
        base64str = base64str.replaceAll("\n|\r", "");
        return base64str;
    }

    /**
     * Base64 javaClass
     */
    public static String serializeClassFilesBase64(Class<?> clazz) {
        String base64str = new sun.misc.BASE64Encoder().encode(ClassFiles.classAsBytes(clazz));
        base64str = base64str.replaceAll("\n|\r", "");
        return base64str;
    }

    /**
     * XStream <= 1.4.17
     */
    public static String serializeXStream(Object object) {
        XStream xstream = new XStream();
        return xstream.toXML(object);
    }

    /**
     * 十六进制
     */
    public static String serializeHexAscii(Object object) throws Exception {
        byte[] bytes = serialize(object);
        int len = bytes.length;
        StringWriter sw = new StringWriter(len * 2);
        for (int i = 0; i < len; ++i)
            addHexAscii(bytes[i], sw);
        return sw.toString();
    }

    static void addHexAscii(byte b, StringWriter sw) {
        int ub = b & 0xff;
        int h1 = ub / 16;
        int h2 = ub % 16;
        sw.write(toHexDigit(h1));
        sw.write(toHexDigit(h2));
    }

    private static char toHexDigit(int h) {
        char out;
        if (h <= 9) out = (char) (h + 0x30);
        else out = (char) (h + 0x37);
        // Printer.error(h + ": " + out);
        return out;
    }


}