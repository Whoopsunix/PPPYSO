package com.ppp.utils.maker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Whoopsunix
 * 加解密
 */
public class Encoder {
    public static String base64encoder(byte[] bytes) throws Exception {
        String base64str = new sun.misc.BASE64Encoder().encode(bytes);
        base64str = base64str.replaceAll("\n|\r", "");
        return base64str;
    }

    public static byte[] base64decoder(String base64Str) throws Exception {
        final byte[] bytes = new sun.misc.BASE64Decoder().decodeBuffer(base64Str);
        return bytes;
    }

    /**
     * Gzip
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(baos);
        gzipOutputStream.write(data);
        gzipOutputStream.close();
        return baos.toByteArray();
    }

}
