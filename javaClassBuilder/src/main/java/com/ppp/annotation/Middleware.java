package com.ppp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 中间件
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {
    // 中间件
    String Tomcat = "Tomcat";
    String Jetty = "Jetty";
    String Spring = "Spring";
    String Undertow = "Undertow";
    String Resin = "Resin";

    String value();

    public static class Utils {
        public static String getTargetMiddleware(String middleware) {
            if (middleware.equalsIgnoreCase(Middleware.Tomcat)) {
                return Middleware.Tomcat;
            } else if (middleware.equalsIgnoreCase(Middleware.Jetty)) {
                return Middleware.Jetty;
            } else {
                return null;
            }
        }
    }
}
