package com.ppp.middleware.builder;

import com.ppp.annotation.Builder;
import com.ppp.annotation.MemShell;
import com.ppp.annotation.Middleware;
import com.ppp.utils.maker.Encoder;
import com.ppp.utils.maker.JavaClassUtils;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

/**
 * @author Whoopsunix
 */
@Builder(Builder.MS)
@Middleware(Middleware.Tomcat)
public class TomcatMSJavaClassBuilder {
    @MemShell(MemShell.Listener)
    public String listener(Class cls) throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(cls));
        classPool.importPackage("javax.servlet.http");

        CtClass ctClass = classPool.getCtClass(cls.getName());


//        CtMethod ctMethod = ctClass.getDeclaredMethod("getResponse");
//
//        ctMethod.setBody("{\n" +
//                "        HttpServletResponse httpServletResponse = null;\n" +
//                "        try {\n" +
//                "            Object request = getFieldValue($1, \"request\");\n" +
//                "            httpServletResponse = (HttpServletResponse) getFieldValue(request, \"response\");\n" +
//                "        } catch (Exception e) {\n" +
//                "\n" +
//                "        }\n" +
//                "        return httpServletResponse;\n" +
//                "    }");


        // 清除所有注解
        JavaClassUtils.clearAllAnnotations(ctClass);


        byte[] classBytes = ctClass.toBytecode();
        String b64 = Encoder.base64encoder(classBytes);
        return b64;
    }
}
