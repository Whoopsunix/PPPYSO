package com.ppp.utils;

import com.ppp.Printer;

import java.util.Arrays;

/**
 * @author Whoopsunix
 */
public class CommandUtils {

    /**
     * 转化为 "" 包裹
     * @param command
     * @return
     */
    public static String splitCommandComma(String command) {
        String[] strings = splitCommand(command);

        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i < strings.length - 1)
                resultBuilder.append("'").append(strings[i]).append("'").append(",");
            else
                resultBuilder.append("'").append(strings[i]).append("'");
        }
        return resultBuilder.toString();

    }

    /**
     * 将命令按空格分为三段用于 new String[]
     * @param command
     * @return
     */
    public static String[] splitCommand(String command) {
        // 使用 String.split() 方法将字符串按空格划分
        String[] parts = command.split("\\s+");

        StringBuilder resultBuilder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i < 2) continue;
            if (i < parts.length - 1)
                resultBuilder.append(parts[i]).append(" ");
            else
                resultBuilder.append(parts[i]);
        }

        if (parts.length >=3){
            String[] result = new String[3];
            result[0] = parts[0];
            result[1] = parts[1];
            result[2] = resultBuilder.toString();
            Printer.blueInfo("Split command: " + Arrays.toString(result));
            return result;
        } else {
            Printer.blueInfo("Split command: " + Arrays.toString(parts));
            return parts;
        }
    }
}
