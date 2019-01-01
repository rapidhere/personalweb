/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.misc;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isStatic;

/**
 * @author rapid
 * @version $Id: MethodHandleHelper.java, v 0.1 2019-01-01 9:04 PM rapid Exp $
 */
@UtilityClass
public class MethodHandleHelper {
    public MethodHandle mh(Class c, String name) {
        return findAndComposeMethodHandle(c, name);
    }

    @SneakyThrows
    private MethodHandle findAndComposeMethodHandle(Class<?> c, String methodName) {
        Optional<Method> method =
            Stream.of(c.getMethods())
                .filter(m -> isStatic(m.getModifiers()) && methodName.equals(m.getName()))
                .findFirst();

        if (method.isPresent()) {
            return MethodHandles.publicLookup().unreflect(method.get());
        } else {
            throw new NoSuchMethodError(methodName + " in " + c.getName());
        }
    }
}