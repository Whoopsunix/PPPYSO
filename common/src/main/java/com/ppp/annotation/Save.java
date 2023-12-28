package com.ppp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Save {
    String Base64 = "Base64";
    String GZIP = "GZIP";
    String Base64gzip  = "Base64gzip";
    String XStream = "XStream";
    String hexAscii = "hexAscii";

    String[] value() default {};
}
