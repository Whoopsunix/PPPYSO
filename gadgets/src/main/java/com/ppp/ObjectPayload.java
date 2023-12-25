package com.ppp;

import com.ppp.sinks.SinksHelper;

/**
 * @author Whoopsunix
 */
public interface ObjectPayload<T> {
    public T getObject(SinksHelper sinksHelper) throws Exception;
}
