package com.ppp.utils;

import java.util.Random;

/**
 * @author Whoopsunix
 *
 * 随机生成工具
 */
public class RanDomUtils {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String CHARACTERSNOTNUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 生成制定长度的随机字符串
     * @param length
     * @return
     */
    public static String generateRandomOnlyString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERSNOTNUM.length());
            char randomChar = CHARACTERSNOTNUM.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * 生成制定长度的随机字符串
     * @param length
     * @return
     */
    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    /**
     * 生成指定长度区间的随机字符串
     * @param minLength
     * @param maxLength
     * @return
     */
    public static String generateRandomString(int minLength, int maxLength) {
        Random random = new Random();
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        return generateRandomString(length);
    }

    public static String generateRandomOnlyString(int minLength, int maxLength) {
        Random random = new Random();
        int length = minLength + random.nextInt(maxLength - minLength + 1);
        return generateRandomOnlyString(length);
    }

    public static void main(String[] args) {
        int length = 1; // 指定字符串长度
        String randomString = generateRandomString(length);
        System.out.println("Random string of length " + length + ": " + randomString);
    }
}
