package com.ppp.utils;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.zip.GZIPInputStream;

/**
 * 反序列化工具类
 */
public class Deserializer implements Callable<Object> {
    private final byte[] bytes;

    public Deserializer(byte[] bytes) {
        this.bytes = bytes;
    }

    public Object call() throws Exception {
        return deserialize(bytes);
    }

    /**
     * original
     */
    public static Object deserializeBase64(final String base64Str) throws Exception {
        final byte[] serialized = new sun.misc.BASE64Decoder().decodeBuffer(base64Str);
        final ByteArrayInputStream in = new ByteArrayInputStream(serialized);
        return deserialize(in);
    }
    public static Object deserialize(final byte[] serialized) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream in = new ByteArrayInputStream(serialized);
        return deserialize(in);
    }

    public static Object deserialize(final InputStream in) throws ClassNotFoundException, IOException {
        final ObjectInputStream objIn = new ObjectInputStream(in);
        return objIn.readObject();
    }

    public static Object deserializeFile(final String filePath) throws ClassNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        return deserialize(fileInputStream);
    }

    /**
     * Gzip
     */
    public static Object deserializeBase64GZip(final String base64Str) throws IOException, ClassNotFoundException {
        final byte[] serialized = new sun.misc.BASE64Decoder().decodeBuffer(base64Str);
        return deserializeGZip(serialized);
    }
    public static Object deserializeGZip(final byte[] serialized) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream in = new ByteArrayInputStream(serialized);
        return deserializeGZip(in);
    }
    public static Object deserializeGZip(final InputStream in) throws ClassNotFoundException, IOException {
        final GZIPInputStream gzipIn = new GZIPInputStream(in);
        final ObjectInputStream objIn = new ObjectInputStream(gzipIn);
        return objIn.readObject();
    }
}