package com.ppp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JavaClass 自定义信息
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaClassModifiable {
    String HEADER = "HEADER";
    String PARAM = "PARAM";
    String PATH = "PATH";
    String[] value() default {};
}
