package com.ppp.sinks.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Whoopsunix
 *
 * 标记 gadget 类型，便于后续统一处理
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sink {
    String InvokerTransformer3 = "InvokerTransformer3";
    String TemplatesImpl = "TemplatesImpl";

    String[] value() default {};

//    public static class Utils {
//        public static String[] getGadgetTypes(AnnotatedElement annotated) {
//            SinkType gadgetTypes = annotated.getAnnotation(SinkType.class);
//            if (gadgetTypes != null && gadgetTypes.value() != null) {
//                return gadgetTypes.value();
//            } else {
//                return new String[0];
//            }
//        }
//    }
}
