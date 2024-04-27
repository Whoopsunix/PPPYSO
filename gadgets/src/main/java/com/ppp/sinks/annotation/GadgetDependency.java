package com.ppp.sinks.annotation;

/**
 * @author Whoopsunix
 */
public enum GadgetDependency {
    Default,

    Rome,
    RomeTools,
    ;

    public static GadgetDependency getGadgetDependency(String name) {
        for (GadgetDependency dependency : GadgetDependency.values()) {
            if (dependency.name().equalsIgnoreCase(name)) {
                return dependency;
            }
        }
        return Default;
    }

}
