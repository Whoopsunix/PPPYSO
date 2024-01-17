package com.ppp;

import com.ppp.annotation.*;
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
                if (javaClassFilePath == null) {
                    Printer.error("Please specify the JavaClass file path");
                }
                Printer.greenInfo("load JavaClass from file: " + javaClassFilePath);
                FileInputStream fileInputStream = new FileInputStream(javaClassFilePath);
                bytes = new byte[fileInputStream.available()];
                fileInputStream.read(bytes);
                fileInputStream.close();
                Printer.greenInfo("Custom JavaClass: " + new String(bytes));
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

        return bytes;

    }

}
