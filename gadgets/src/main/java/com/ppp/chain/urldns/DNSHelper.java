package com.ppp.chain.urldns;

import java.util.ArrayList;

/**
 * @author Whoopsunix
 * <p>
 * URLDNS
 */
public class DNSHelper {
    private String host;
    private Product[] products;

    /**
     * 自定义
     */
    private String className;
    private String subdomain;

    public DNSHelper() {
        this.products = new Product[]{};
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
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
