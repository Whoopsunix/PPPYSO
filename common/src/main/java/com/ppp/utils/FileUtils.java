package com.ppp.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

/**
 * @author Whoopsunix
 */
public class FileUtils {
    public static void main(String[] args) throws Exception {
        splitFile("/tmp/busybox", 1024 * 100);
    }

    /**
     * 文件分片 1kb 为单位
     * @param localFilePath
     * @param splitLength
     * @return
     * @throws Exception
     */
    public static List<byte[]> splitFile(String localFilePath, int splitLength) throws Exception {
        splitLength = splitLength * 1024;
        List<byte[]> parts = new ArrayList<byte[]>();
        File inputFile = new File(localFilePath);
        FileInputStream fileInputStream = new FileInputStream(inputFile);

        byte[] buffer = new byte[splitLength];
        int bytesRead;

        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            byte[] part = new byte[bytesRead];
            System.arraycopy(buffer, 0, part, 0, bytesRead);
            parts.add(part);
        }

        fileInputStream.close();
        return parts;
    }

    /**
     * 读取文件
     * @param localFilePath
     * @return
     * @throws Exception
     */
    public static byte[] fileRead(String localFilePath) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(localFilePath);
        byte[] contentBytes = new byte[fileInputStream.available()];
        fileInputStream.read(contentBytes);
        fileInputStream.close();
        return contentBytes;
    }
}
