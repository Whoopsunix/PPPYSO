package com.ppp.utils.maker;

import com.ppp.utils.Reflections;

import java.lang.annotation.Annotation;

/**
 * @author Whoopsunix
 * 注解工具类
 */
public class AnnotationUtils {
    /**
     * 注解数组是否含有指定注解
     *
     * @param values
     * @param targetValue
     * @return
     */
    public static boolean containsValue(String[] values, String targetValue) {
        for (String value : values) {
            if (value.equals(targetValue)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsValue(Class clazz, Class<? extends Annotation> anno, String targetValue) throws Exception{
        Annotation annotation = clazz.getAnnotation(anno);

        String value = (String) Reflections.invokeMethod(annotation, "value", new Class[]{}, new Object[]{});

        if (annotation != null && value.equals(targetValue)) {
            return true;
        }

        return false;
    }

}
