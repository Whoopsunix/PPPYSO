package com.ppp.middleware.tomcat;

import com.ppp.utils.Encoder;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.Base64;

/**
 * @author Whoopsunix
 */
public class TomcatMSJavaClassMaker {
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


        byte[] classBytes = ctClass.toBytecode();
        String b64 = Encoder.base64encoder(classBytes);
        return b64;
    }
}
