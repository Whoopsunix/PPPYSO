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
    public String defaultJNDI(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        Printer.yellowInfo("jndi url: " + command);

        return command;
    }

    @EnchantType({EnchantType.JRMP})
    public void defaultJRMP(SinksHelper sinksHelper) {
        String host = sinksHelper.getHost();
        Integer port = sinksHelper.getPort();

        Printer.yellowInfo("host: " + host);
        Printer.yellowInfo("port: " + port);
    }
}
