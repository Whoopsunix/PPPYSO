package com.ppp.utils;

import java.util.Arrays;

/**
 * @author Whoopsunix
 */
public class UTF8Serializer {
    public static void main(String[] args) {

    }

    public static byte[] resultBytes = new byte[0];

    final static byte TC_CLASSDESC = (byte) 0x72;

    public static byte[] builder(byte[] originalBytes) {
        for (int i = 0; i < originalBytes.length; i++) {
            byte b = originalBytes[i];
            byteAdd(b);
            if (b == TC_CLASSDESC) {
                int length = changeTC_CLASSDESC(originalBytes, i);
                if (length != -1) {
                    i += length;
                }
            }
        }

        System.out.println(Arrays.toString(resultBytes));

        return resultBytes;
    }

    public static int changeTC_CLASSDESC(byte[] originalBytes, int index) {
        // 字符长度
        int length = ((originalBytes[index + 1] & 0xFF) << 8) | (originalBytes[index + 2] & 0xFF);
        // 过场可能报错
        if (length > 0xff)
            return -1;

        // 原始内容
        byte[] originalValue = new byte[length];
        System.arraycopy(originalBytes, index + 3, originalValue, 0, length);
        // 非全部可见字符，可能存在的报错，不继续执行
        if (!isByteVisible(originalValue)) {
            return -1;
        }
        System.out.println(Arrays.toString(originalValue));
        System.out.println(new String(originalValue));


        encode(originalValue);
        return length + 2;
    }

    /**
     * 加密
     *
     * @return
     */
    public static void encode(byte[] originalValue) {
        int newLength = originalValue.length * 2;

        byteAdd((byte) ((newLength >> 8) & 0xFF));
        byteAdd((byte) (newLength & 0xFF));

        for (int i = 0; i < originalValue.length; i++) {
            char c = (char) originalValue[i];
            byteAdd((byte) (0xC0 | ((c >> 6) & 0x1F)));
            byteAdd((byte) (0x80 | ((c >> 0) & 0x3F)));
        }
    }


    /**
     * 判断字节是否在可见字符的 ASCII 范围内
     *
     * @param bytes
     * @return
     */
    public static boolean isByteVisible(byte[] bytes) {
        for (byte b : bytes) {
            if (b < 32 || b > 126) {
                return false;
            }
        }
        return true;
    }

    /**
     * 追加 byte
     *
     * @param bytes
     * @param b
     * @return
     */
    public static void byteAdd(byte[] bytes, byte b) {
        byte[] newBytes = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        newBytes[bytes.length] = b;
        bytes = newBytes;
    }

    public static void byteAdd(byte b) {
        byte[] newBytes = new byte[resultBytes.length + 1];
        System.arraycopy(resultBytes, 0, newBytes, 0, resultBytes.length);
        newBytes[resultBytes.length] = b;
        resultBytes = newBytes;
    }

    /**
     * 追加 byte[]
     *
     * @param bytes
     * @param bytesAdd
     * @return
     */
    public static void byteAdd(byte[] bytes, byte[] bytesAdd) {
        byte[] newBytes = new byte[bytes.length + bytesAdd.length];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        System.arraycopy(bytesAdd, 0, newBytes, bytes.length, bytesAdd.length);
        bytes = newBytes;
//        return newBytes;
    }

}
