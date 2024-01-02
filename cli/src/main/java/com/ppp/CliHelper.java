package com.ppp;

/**
 * @author Whoopsunix
 */
public class CliHelper {
    private Class<? extends ObjectPayload> gadgetClass;

    public Class<? extends ObjectPayload> getGadgetClass() {
        return gadgetClass;
    }

    public void setGadgetClass(Class<? extends ObjectPayload> gadgetClass) {
        this.gadgetClass = gadgetClass;
    }
}
