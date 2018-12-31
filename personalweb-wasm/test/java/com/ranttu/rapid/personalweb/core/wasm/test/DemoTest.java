/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.test;

import com.ranttu.rapid.personalweb.core.wasm.compile.WebAssemblyCompiler;
import lombok.experimental.var;
import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: DemoTest.java, v 0.1 2018-12-09 2:12 PM rapid Exp $
 */
public class DemoTest {
    private int c;

    @Test
    public void test0() {
        var stream = getClass()
            .getResourceAsStream("/testres/demo2.wasm");
        var module = new WebAssemblyCompiler().compile(stream);

        System.out.println(module);
    }
}