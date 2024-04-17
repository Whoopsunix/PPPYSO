package com.ppp.annotation;

import com.ppp.Printer;

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
        public static String getMiddleware(String middleware) {
            if (middleware != null && middleware.equalsIgnoreCase(Middleware.Tomcat)) {
                return Middleware.Tomcat;
            } else if (middleware != null && middleware.equalsIgnoreCase(Middleware.Jetty)) {
                return Middleware.Jetty;
            } else if (middleware != null && middleware.equalsIgnoreCase(Middleware.Spring)) {
                return Middleware.Spring;
            } else if (middleware != null && middleware.equalsIgnoreCase(Middleware.Undertow)) {
                return Middleware.Undertow;
            } else if (middleware != null && middleware.equalsIgnoreCase(Middleware.Resin)) {
                return Middleware.Resin;
            } else {
                Printer.error(String.format("Middleware not found: %s", middleware));
                return null;
            }
        }
    }
}
