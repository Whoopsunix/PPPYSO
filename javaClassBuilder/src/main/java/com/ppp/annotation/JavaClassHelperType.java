package com.ppp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JavaClass 类型
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JavaClassHelperType {
    String MemShell = "MemShell";
    String RceEcho = "RceEcho";
    // 自定义
    String Custom = "Custom";

    String value();
}
