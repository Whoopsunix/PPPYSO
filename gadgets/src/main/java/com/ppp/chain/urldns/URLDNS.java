package com.ppp.chain.urldns;

import com.ppp.ObjectPayload;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import org.apache.commons.collections.Transformer;

/**
 * @author Whoopsunix
 *
 * Gadget Chain:
 * HashMap.readObject()
 *      HashMap.putVal()
 *          HashMap.hash()
 *              URL.hashCode()
 */
@Sink({Sink.URLDNS})
public class URLDNS implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(URLDNS.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        return null;
    }

    public Object getChain(Transformer[] transformers) throws Exception {
        return null;
    }
}
