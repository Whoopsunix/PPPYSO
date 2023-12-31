package com.ppp;

import com.ppp.utils.Reflections;

import java.lang.reflect.InvocationHandler;
import java.util.Map;

/**
 * @author Whoopsunix
 */
public class KickOff {

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    /**
     * sun.reflect.annotation.AnnotationInvocationHandler
     */
    public static InvocationHandler annotationInvocationHandler(final Map map) throws Exception {
        final InvocationHandler handler = annotationInvocationHandler(map, Override.class);
        return handler;
    }

    public static InvocationHandler annotationInvocationHandler(final Map map, final Class ifaces) throws Exception {
        final InvocationHandler handler = (InvocationHandler) Reflections.getFirstCtor(ANN_INV_HANDLER_CLASS).newInstance(ifaces, map);
        return handler;
    }

}
