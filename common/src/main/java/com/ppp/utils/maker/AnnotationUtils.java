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

    public static boolean containsValue(Class clazz, Class<? extends Annotation> anno, String targetValue) {
        try {
            Annotation annotation = clazz.getAnnotation(anno);
            if (annotation == null)
                return false;

            Object value = Reflections.invokeMethod(annotation, "value", new Class[]{}, new Object[]{});

            if (value instanceof String[])
                return containsValue((String[]) value, targetValue);
            else if (value instanceof String) {
                return value.equals(targetValue);
            }
        } catch (Exception e) {

        }

        return false;
    }

}
