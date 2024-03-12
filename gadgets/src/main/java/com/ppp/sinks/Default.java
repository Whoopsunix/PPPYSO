package com.ppp.sinks;

import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;

import java.io.FileInputStream;

/**
 * @author Whoopsunix
 * <p>
 * 默认 无返回值
 */
@Sink({Sink.Default})
public class Default {
    @EnchantType({EnchantType.DEFAULT})
    public String defaultCommand(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        Printer.yellowInfo("command: " + command);

        return command;
    }


    /**
     * 文件写入
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.FileWrite})
    public byte[] fileWrite(SinksHelper sinksHelper) throws Exception {
        String serverFilePath = sinksHelper.getServerFilePath();
        String localFilePath = sinksHelper.getLocalFilePath();
        String fileContent = sinksHelper.getFileContent();
        Printer.yellowInfo("Server file path: " + serverFilePath);

        byte[] contentBytes = new byte[]{};

        if (localFilePath != null) {
            Printer.yellowInfo("Local file path: " + localFilePath);
            try {
                FileInputStream fileInputStream = new FileInputStream(localFilePath);
                contentBytes = new byte[fileInputStream.available()];
                fileInputStream.read(contentBytes);
                fileInputStream.close();
            } catch (Exception e) {
                Printer.error("File read error");
            }
        } else if (fileContent != null) {
            contentBytes = fileContent.getBytes();
        }

        return contentBytes;
    }
}
