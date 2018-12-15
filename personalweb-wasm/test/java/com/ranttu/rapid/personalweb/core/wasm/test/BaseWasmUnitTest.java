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
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author rapid
 * @version $Id: BaseWasmUnitTest.java, v 0.1 2018Äê12ÔÂ15ÈÕ 7:05 PM rapid Exp $
 */
abstract public class BaseWasmUnitTest {
    private static ThreadLocal<Object> modules = new ThreadLocal<>();

    /**
     * load wasm instance
     */
    @BeforeMethod
    protected void allocateModule() {
        var stream = getClass()
            .getResourceAsStream("/testres/unit/" + getClass().getSimpleName() + ".wasm");

        var module = new WebAssemblyCompiler().compile(stream);
        modules.set(module);
    }

    @SneakyThrows
    protected static Object callFunc(String name, Object... args) {
        var module = modules.get();

        Optional<Method> m = Stream.of(module.getClass().getMethods())
            .filter(method -> method.getName().equals(name))
            .findFirst();

        if (m.isPresent()) {
            return m.get().invoke(module, args);
        } else {
            throw new NoSuchMethodException(name);
        }
    }
}