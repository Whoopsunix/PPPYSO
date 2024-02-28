package com.ppp.sinks;

import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;

/**
 * @author Whoopsunix
 */
@Sink({Sink.JNDI})
public class JNDI {
    @EnchantType({EnchantType.DEFAULT})
    public String defaultJndi(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        Printer.yellowInfo("jndi url: " + command);

        return command;
    }
}
