package com.ppp.sinks;

import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;

/**
 * @author Whoopsunix
 * <p>
 * EL Expression
 * MyFaces 没复现出来
 */
@Sink({Sink.EL})
public class EL {
    @EnchantType({EnchantType.DEFAULT})
    public String defaultEL(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();

        Printer.yellowInfo("el: " + sinksHelper.getCommand());

        return command;
    }
}
