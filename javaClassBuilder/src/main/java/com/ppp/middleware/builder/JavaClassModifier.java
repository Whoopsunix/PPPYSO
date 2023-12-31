package com.ppp.middleware.builder;

import com.ppp.JavaClassHelper;
import com.ppp.annotation.JavaClassModifiable;
import com.ppp.utils.maker.AnnotationUtils;
import com.ppp.utils.maker.JavaClassUtils;
import javassist.CtClass;

/**
 * @author Whoopsunix
 */
public class JavaClassModifier {
    public static void fieldChange(Class cls, CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        // 适用于自定义内存马的时候 直接跳过
        if (javaClassHelper == null)
            return;

        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.HEADER)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.HEADER, String.format("private static String %s = \"%s\";",JavaClassModifiable.HEADER, javaClassHelper.getHEADER()));
        }

        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.PARAM)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.PARAM, String.format("private static String %s = \"%s\";",JavaClassModifiable.PARAM, javaClassHelper.getPARAM()));
        }
    }


}
