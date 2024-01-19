package com.ppp.chain.urldns;

import java.util.ArrayList;

/**
 * @author Whoopsunix
 *
 * URLDNS
 */
public class DNSHelper {
    private String host;
    private ArrayList<String> products;

    /**
     * 自定义
     */
    private String className;
    private String subdomain;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }
}
