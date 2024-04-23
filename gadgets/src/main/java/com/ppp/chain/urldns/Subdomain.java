package com.ppp.chain.urldns;

import com.ppp.Printer;

/**
 * @author Whoopsunix
 */
public enum Subdomain {
    // JDK
    BCEL(Product.BCEL.getLongProduct(), "bcel", "com.sun.org.apache.bcel.internal.util.ClassLoader"),
    JDK7u21(Product.JDK7u21.getLongProduct(), "jdk7u21", "com.sun.corba.se.impl.orbutil.ORBClassLoader"),
    JDK8u20(Product.JDK8u20.getLongProduct(), "jdk8u20", "javax.swing.plaf.metal.MetalFileChooserUI$DirectoryComboBoxModel$1"),
    Linux(Product.OS.getLongProduct(), "linux", "sun.awt.X11.AwtGraphicsConfigData"),
    Windows(Product.OS.getLongProduct(), "win", "sun.awt.windows.WButtonPeer"),

    // 组件
    // 3.0 org.apache.commons.collections.map.PredicatedMap$PredicatedMapEntry
    // 3.1 org.apache.commons.collections.collection.AbstractSerializableCollectionDecorator
    // 3.2 org.apache.commons.collections.buffer.BoundedBuffer
    // 3.2.1 org.apache.commons.collections.DoubleOrderedMap$1$1
    // 3.2.2 org.apache.commons.collections.functors.FunctorUtils$1
    cc31(Product.CommonsCollections3.getLongProduct(), "cc31", "org.apache.commons.collections.collection.AbstractSerializableCollectionDecorator"),
    cc321(Product.CommonsCollections3.getLongProduct(), "cc321", "org.apache.commons.collections.DoubleOrderedMap$1$1"),
    cc322(Product.CommonsCollections3.getLongProduct(), "cc322", "org.apache.commons.collections.functors.FunctorUtils$1"),

    cc40(Product.CommonsCollections4.getLongProduct(), "cc40", "org.apache.commons.collections4.functors.ChainedTransformer"),
    cc41(Product.CommonsCollections4.getLongProduct(), "cc41", "org.apache.commons.collections4.FluentIterable"),

    cb17(Product.CommonsBeanutils.getLongProduct(), "cb17", "org.apache.commons.beanutils.MappedPropertyDescriptor$1"),
    cb18x(Product.CommonsBeanutils.getLongProduct(), "cb18x", "org.apache.commons.beanutils.DynaBeanMapDecorator$MapEntry"),
    cb19x(Product.CommonsBeanutils.getLongProduct(), "cb19x", "org.apache.commons.beanutils.BeanIntrospectionData"),

    c3p092x(Product.C3P0.getLongProduct(), "c3p092x", "com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase"),
    c3p095(Product.C3P0.getLongProduct(), "c3p095", "com.mchange.v2.c3p0.test.AlwaysFailDataSource"),

    ajw(Product.AspectJWeaver.getLongProduct(), "ajw", "org.aspectj.weaver.tools.cache.SimpleCache"),

    // serialVersionUID
    // 2.0b4   4949939576606791809
    // 2.0b5   4041428789013517368,2.0.b6 无法反序列化
    bsh20b4(Product.Bsh.getLongProduct(), "bsh20b4", "bsh.CollectionManager$1"),
    bsh20b5(Product.Bsh.getLongProduct(), "bsh20b5", "bsh.engine.BshScriptEngine"),
    bsh20b6(Product.Bsh.getLongProduct(), "bsh20b6", "bsh.collection.CollectionIterator$1"),

    // serialVersionUID
    // 1.7.0-2.4.3 serialVersionUID
    // 2.4.x   -8137949907733646644
    // 2.3.x   1228988487386910280
    groovy1702311(Product.Groovy.getLongProduct(), "groovy1702311", "org.codehaus.groovy.reflection.ClassInfo$ClassInfoSet"),
    groovy24x(Product.Groovy.getLongProduct(), "groovy24x", "groovy.lang.Tuple2"),
    groovy244(Product.Groovy.getLongProduct(), "groovy244", "org.codehaus.groovy.runtime.dgm$1170"),

    rome1000(Product.ROME.getLongProduct(), "rome1000", "com.sun.syndication.feed.impl.ToStringBean"),
    rome1111(Product.ROME.getLongProduct(), "rome1111", "com.rometools.rome.feed.impl.ObjectBean"),

    fastjson(Product.Fastjson.getLongProduct(), "fastjson", "com.alibaba.fastjson.JSONArray"),
    fastjson2(Product.Fastjson.getLongProduct(), "fastjson2", "com.alibaba.fastjson2.JSONArray"),

    jackson(Product.Jackson.getLongProduct(), "jackson", "com.fasterxml.jackson.databind.node.NodeSerialization"),

    // fastjson<=1248存在一个链,全版本需要用 hashMap 绕过 checkAutoType
    // 此链依赖 BadAttributeValueExpException,在JDK1.7中无法使用.此时需要用 springAOP 绕过
    // fastjon/jackson 两个链的变种都需要 springAOP
    springaop(Product.SpringAOP.getLongProduct(), "springaop", "org.springframework.aop.target.HotSwappableTargetSource.HotSwappableTargetSource"),
    ;

    private final String product;
    private final String subdomain;
    private final String className;

    public String getProduct() {
        return product;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public String getClassName() {
        return className;
    }

    Subdomain(String product, String subdomain, String className) {
        this.product = product;
        this.subdomain = subdomain;
        this.className = className;
    }


    public static void show() {
        for (Subdomain subdomainEnum : Subdomain.values()) {
            Printer.blueInfo(subdomainEnum.getProduct() + " | " + subdomainEnum.getSubdomain() + " | " + subdomainEnum.getClassName());
        }
    }

}
