package com.ppp.utils;

import java.util.Random;

/**
 * @author Whoopsunix
 *
 * 随机生成工具
 */
public class RanDomUtils {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

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

    public static void main(String[] args) {
        int length = 1; // 指定字符串长度
        String randomString = generateRandomString(length);
        System.out.println("Random string of length " + length + ": " + randomString);
    }
}
