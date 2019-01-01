/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.test;

import com.ranttu.rapid.personalweb.core.wasm.compile.WebAssemblyCompiler;
import lombok.SneakyThrows;
import lombok.experimental.var;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author rapid
 * @version $Id: BaseWasmUnitTest.java, v 0.1 2018-12-15 7:05 PM rapid Exp $
 */
abstract public class BaseWasmUnitTest {
    protected static ThreadLocal<Object> modules = new ThreadLocal<>();

    /**
     * load wasm instance
     */
    @BeforeMethod
    protected void allocateModule() {
        String path = "/testres/unit/" + getClass().getSimpleName() + ".wasm";
        var stream = getClass()
            .getResourceAsStream(path);

        var module = new WebAssemblyCompiler().compile(stream, path);
        modules.set(module);
    }

    @SneakyThrows
    protected static Object callFunc(String name, Object... args) {
        var module = modules.get();

        Optional<Method> m = Stream.of(module.getClass().getMethods())
            .filter(method -> method.getName().equals(name))
            .findFirst();
        var argList = new ArrayList<Object>();
        Collections.addAll(argList, args);

        if (m.isPresent()) {
            return m.get().invoke(module, argList.toArray());
        } else {
            throw new NoSuchMethodException(name);
        }
    }
}