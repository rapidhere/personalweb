/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.rt;

import com.ranttu.rapid.personalweb.core.wasm.misc.MethodHandleHelper;
import lombok.experimental.UtilityClass;

import java.lang.invoke.MethodHandle;
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

    // method handles
    public final MethodHandle MH_OBJECT_IDENTITY = MethodHandleHelper.mh(Runtimes.class, "objectIdentity");
    public final MethodHandle MH_OBTAIN_OBJECT = MethodHandleHelper.mh(Runtimes.class, "obtainObject");

    // TODO: refine
    @SuppressWarnings("unused")
    public int objectIdentity(Object x) {
        int identity = System.identityHashCode(x);
        OBJECTS.put(identity, x);
        return identity;
    }

    // TODO: refine
    @SuppressWarnings("unused")
    public Object obtainObject(int identity) {
        return OBJECTS.get(identity);
    }
}