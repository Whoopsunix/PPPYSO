package com.ppp.sinks;

import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;

/**
 * @author Whoopsunix
 * <p>
 * 默认
 */
@Sink({Sink.Command})
public class Command {
    @EnchantType({EnchantType.DEFAULT})
    public String defaultJndi(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        Printer.yellowInfo("command: " + command);

        return command;
    }
}
