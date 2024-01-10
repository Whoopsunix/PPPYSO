package com.ppp;

/**
 * @author Whoopsunix
 */
public class Printer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_ORANGE = "\u001B[38;5;208m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String PRINT_ENABLED_PROPERTY = "pppyso.printer.printEnabled";

    private static boolean isPrintEnabled() {
        return Boolean.parseBoolean(System.getProperty(PRINT_ENABLED_PROPERTY, "true"));
    }

    /**
     * 原始输出
     * @param msg
     */
    public static void print(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println(msg);
        System.out.flush();
    }

    /**
     * 标题
     * @param msg
     */
    public static void title(Object msg) {
        if (!isPrintEnabled())
            return;
        int totalLength = 50;
        int paddingLength = totalLength - String.valueOf(msg).length();
        int leftPadding = paddingLength / 2;
        int rightPadding = paddingLength - leftPadding;

        StringBuilder paddedString = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            paddedString.append('-');
        }

        paddedString.append(msg);

        for (int i = 0; i < rightPadding; i++) {
            paddedString.append('-');
        }

        System.out.println(paddedString);
        System.out.flush();
    }

    /**
     * payload 配置相关
     * @param msg
     */
    public static void info(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println("# " + msg);
        System.out.flush();
    }
    /**
     * payload 醒目配置 绿色，针对该 Payload 的配置项
     * @param msg
     */
    public static void greenInfo(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println(ANSI_GREEN + "# " + msg + ANSI_RESET);
        System.out.flush();
    }
    /**
     * payload 醒目配置 蓝色，根据输入动态生成的重要信息，用于提示
     * @param msg
     */
    public static void blueInfo(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println(ANSI_BLUE + "# " + msg + ANSI_RESET);
        System.out.flush();
    }
    /**
     * 运行时 主要是 exploit 和 部分链生成时的输出
     * @param msg
     */
    public static void log(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println("* " + msg);
        System.out.flush();
    }
    /**
     * 警告
     * @param msg
     */
    public static void warn(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println(ANSI_ORANGE + "! Warning: " + msg + ANSI_RESET);
        System.out.flush();
    }
    /**
     * 错误 伴随着程序结束
     * @param msg
     */
    public static void error(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println(ANSI_RED + "! Error: " + msg + ANSI_RESET);
        System.out.flush();
        System.exit(0);
    }
    /**
     * 调试
     * @param msg
     */
    public static void debug(Object msg) {
        if (!isPrintEnabled())
            return;
        System.out.println("# Debug: " + msg);
        System.out.flush();
    }
}
