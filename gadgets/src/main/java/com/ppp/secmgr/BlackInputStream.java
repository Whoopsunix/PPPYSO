package com.ppp.secmgr;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class BlackInputStream extends ObjectInputStream {
    private Set blackList = new HashSet() {
        {
            add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
            add("com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter");
         add("org.apache.commons.collections.functors.InvokerTransformer");
//         add("java.security.SignedObject");
        }
    };

    public BlackInputStream(InputStream in) throws IOException {
        super(in);
    }


    protected Class resolveClass(ObjectStreamClass cls) throws IOException, ClassNotFoundException {
        System.out.println(cls);
        if (blackList.contains(cls.getName())) {
            throw new InvalidClassException("Unexpected serialized class", cls.getName());
        } else {
            return super.resolveClass(cls);
        }
    }
}
