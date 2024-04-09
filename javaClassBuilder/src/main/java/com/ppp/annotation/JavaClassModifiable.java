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
    // 类名
    String CLASSNAME = "CLASSNAME";
    // 内存马名称
    String NAME = "NAME";
    // 请求头 key
    String HEADER = "HEADER";
    // 参数 key
    String PARAM = "PARAM";
    // 路径
    String PATH = "PATH";

    // Godzilla
    String key = "key";
    String pass = "pass";
    String lockHeaderKey = "lockHeaderKey";
    String lockHeaderValue = "lockHeaderValue";
    String[] value() default {};
}
