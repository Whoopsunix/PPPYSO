package com.ppp.middleware.builder;

import com.ppp.JavaClassHelper;
import com.ppp.annotation.MemShellModifiable;
import com.ppp.utils.maker.AnnotationUtils;
import com.ppp.utils.maker.JavaClassUtils;
import javassist.CtClass;

/**
 * @author Whoopsunix
 */
public class MemShellModifier {
    public static void fieldChange(Class cls, CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        // 适用于自定义内存马的时候 直接跳过
        if (javaClassHelper == null)
            return;

        if (AnnotationUtils.containsValue(cls, MemShellModifiable.class, MemShellModifiable.HEADER)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, "HEADER", String.format("private static String NAME = \"%s\";", javaClassHelper.getHEADER()));
        }
    }


}
