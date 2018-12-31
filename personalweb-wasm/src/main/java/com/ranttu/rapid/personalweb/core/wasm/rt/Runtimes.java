/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.rt;

import lombok.experimental.UtilityClass;

import java.util.HashMap;

/**
 * runtime utilities
 *
 * @author rapid
 * @version $Id: Runtimes.java, v 0.1 2018-12-31 9:41 PM rapid Exp $
 */
@UtilityClass
public class Runtimes {
    private final HashMap<Integer, Object> OBJECTS = new HashMap<>();

    // TODO: refine
    public int objectIdentity(Object x) {
        int identity = System.identityHashCode(x);
        OBJECTS.put(identity, x);
        return identity;
    }

    // TODO: refine
    public Object obtainObject(int identity) {
        return OBJECTS.get(identity);
    }
}