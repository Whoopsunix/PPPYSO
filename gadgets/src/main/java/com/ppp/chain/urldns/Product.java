package com.ppp.chain.urldns;

import com.ppp.Printer;

import java.util.Arrays;

/**
 * @author Whoopsunix
 */
public enum Product {
    // JDK
    BCEL("bcel", "bcel"),
    JDK7u21("JDK7u21", "JDK7u21"),
    JDK8u20("JDK8u20", "JDK8u20"),
    OS("os", "os"),

    // 组件
    CommonsCollections3("cc3", "CommonsCollections3"),
    CommonsCollections4("cc4", "CommonsCollections4"),
    CommonsBeanutils("cb", "CommonsBeanutils"),
    C3P0("c3p0", "c3p0"),
    Bsh("bsh", "bsh"),
    Groovy("Groovy", "Groovy"),
    ROME("ROME", "ROME"),
    Fastjson("Fastjson", "Fastjson"),
    Jackson("Jackson", "Jackson"),
    SpringAOP("SpringAOP", "SpringAOP"),
    AspectJWeaver("ajw", "AspectJWeaver"),
    ;

    private final String product;
    private final String longProduct;

    Product(String product, String longProduct) {
        this.product = product;
        this.longProduct = longProduct;
    }

    public String getProduct() {
        return product;
    }

    public String getLongProduct() {
        return longProduct;
    }

    public static Product getProduct(String product) {
        for (Product p : Product.values()) {
            if (p.getProduct().equalsIgnoreCase(product) || p.getLongProduct().equalsIgnoreCase(product)) {
                return p;
            }
        }
        Printer.warn("Product not found: " + product);
        Product.show();
        return null;
    }

    public static void show() {
        Printer.blueInfo("Current support products: " + Arrays.toString(Product.values()));
    }
}
