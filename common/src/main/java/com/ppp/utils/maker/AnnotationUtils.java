package com.ppp.utils.maker;

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

}
