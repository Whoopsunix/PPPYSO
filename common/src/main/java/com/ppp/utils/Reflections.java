package com.ppp.utils;

import sun.reflect.ReflectionFactory;

import java.lang.reflect.*;

@SuppressWarnings("restriction")
public class Reflections {

    public static void setAccessible(AccessibleObject member) {
        member.setAccessible(true);
    }

    public static Field getField(final Class<?> clazz, final String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static void setFieldValue(final Object obj, final String fieldName, final Object value) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static Object getFieldValue(final Object obj, final String fieldName) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        return field.get(obj);
    }

    public static Constructor<?> getFirstCtor(final String name) throws Exception {
        final Constructor<?> constructor = Class.forName(name).getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        return constructor;
    }

    public static Constructor<?> getFirstCtor(Class clazz) throws Exception {
        final Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        return constructor;
    }

    public static Object newInstance(String className, Object... args) throws Exception {
        return getFirstCtor(className).newInstance(args);
    }

    public static Object newInstance(Class cls, Class[] argsClass, Object[] args) throws Exception {
        Constructor<?> constructor = cls.getDeclaredConstructor(argsClass);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }

    public static <T> T createWithoutConstructor(Class<T> classToInstantiate)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return createWithConstructor(classToInstantiate, Object.class, new Class[0], new Object[0]);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T createWithConstructor(Class<T> classToInstantiate, Class<? super T> constructorClass, Class<?>[] consArgTypes, Object[] consArgs)
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<? super T> objCons = constructorClass.getDeclaredConstructor(consArgTypes);
        objCons.setAccessible(true);
        Constructor<?> sc = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(classToInstantiate, objCons);
        sc.setAccessible(true);
        return (T) sc.newInstance(consArgs);
    }

    public static Object invokeMethod(Object obj, String methodName, Class[] argsClass, Object[] args) throws Exception {
        Method method;
        try {
            method = obj.getClass().getDeclaredMethod(methodName, argsClass);
        } catch (NoSuchMethodException e) {
            method = obj.getClass().getSuperclass().getDeclaredMethod(methodName, argsClass);
        }
        method.setAccessible(true);
        Object object = method.invoke(obj, args);
        return object;
    }

    public static Object invokeMethod(Object obj, String methodName, Object... args) throws Exception {
        Class<?>[] argsClass = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClass[i] = args[i].getClass();
            if (argsClass[i].equals(Integer.class))
                argsClass[i] = Integer.TYPE;
            else if (argsClass[i].equals(Boolean.class))
                argsClass[i] = Boolean.TYPE;
            else if (argsClass[i].equals(Byte.class))
                argsClass[i] = Byte.TYPE;
            else if (argsClass[i].equals(Long.class))
                argsClass[i] = Long.TYPE;
            else if (argsClass[i].equals(Double.class))
                argsClass[i] = Double.TYPE;
            else if (argsClass[i].equals(Float.class))
                argsClass[i] = Float.TYPE;
            else if (argsClass[i].equals(Character.class))
                argsClass[i] = Character.TYPE;
            else if (argsClass[i].equals(Short.class))
                argsClass[i] = Short.TYPE;
        }
        Method method = obj.getClass().getDeclaredMethod(methodName, argsClass);
        Object o = method.invoke(obj, args);
        return o;
    }

}
