package com.ppp.utils;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;

import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * @author Whoopsunix
 */
public class JavaClassUtils {
    /**
     * 是否存在 Filed
     *
     * @param ctClass
     * @param fieldName
     * @return
     */
    public static boolean fieldCheck(CtClass ctClass, String fieldName) {
        try {
            ctClass.getDeclaredField(fieldName);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    /**
     * 生成 byte[]
     *
     * @param ctClass
     * @param className
     * @param payloadOptions
     * @return
     * @throws Exception
     */
    public static byte[] ctClassScheduler(CtClass ctClass, String className, HashMap payloadOptions) throws Exception {
        // 预先设定类名
        if (className != null) {
            ctClass.setName(className);
        }

        // JavaClass info update
        javaClassInfo(ctClass, payloadOptions);
//        Printer.title("javaClass Info");
//        Printer.blueInfo("javaClass name: " + ctClass.getName());

//        // 移除类文件部分属性
//        ClassFile classFile = ctClass.getClassFile();
//        // 源文件信息
//        classFile.removeAttribute(SourceFileAttribute.tag);
//        // 移除行号信息
//        classFile.removeAttribute(LineNumberAttribute.tag);
//        classFile.removeAttribute(LocalVariableAttribute.tag);
//        classFile.removeAttribute(LocalVariableAttribute.typeTag);
//        classFile.removeAttribute(DeprecatedAttribute.tag);
//        classFile.removeAttribute(SignatureAttribute.tag);
//        classFile.removeAttribute(StackMapTable.tag);

        // jdk5
        ctClass.getClassFile().setVersionToJava5();
        byte[] classBytes = ctClass.toBytecode();
        // jdk 6
//        classBytes[7] = 49;


        // 保存文件
        Object save = payloadOptions.get("save");
        if (save != null) {
            saveJavaClass(ctClass, classBytes);
        }
        return classBytes;
    }

    /**
     * 存储生成的 JavaClass
     *
     * @param ctClass
     * @param classBytes
     */
    public static void saveJavaClass(CtClass ctClass, byte[] classBytes) {
        try {
            String fileName = ctClass.getSimpleName() + ".class";
            String filePath = "./";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + fileName);
//            Printer.info("save JavaClass as file " + filePath + fileName);
            fileOutputStream.write(classBytes);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
//            Printer.error(e.getMessage());
        }
    }

    /**
     * javaClass 信息修改
     *
     * @param ctClass
     * @param payloadOptions
     */
    public static void javaClassInfo(CtClass ctClass, HashMap payloadOptions) {
        ctClass.defrost();
        // 修改类名
        Object javaClassName = payloadOptions.get("javaClassName");
        if (javaClassName != null) {
//            Printer.info("set class name " + javaClassName);
            ctClass.setName(javaClassName.toString());
        }
    }

    /**
     * javassist 插入字段 只修改已存在的字段
     *
     * @param memShellCtClass
     * @param fieldName
     * @param fieldCode
     */
    public static boolean fieldChangeIfExist(CtClass memShellCtClass, String fieldName, String fieldCode) {
        try {
            memShellCtClass.defrost();
            CtField ctField = memShellCtClass.getDeclaredField(fieldName);
            memShellCtClass.removeField(ctField);
            memShellCtClass.addField(CtField.make(fieldCode, memShellCtClass));
            return true;
        } catch (NotFoundException e) {

        } catch (CannotCompileException e) {

        }
        return false;
    }
}
