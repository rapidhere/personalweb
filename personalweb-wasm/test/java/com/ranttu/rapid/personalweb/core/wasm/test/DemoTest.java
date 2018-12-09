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
 * @version $Id: DemoTest.java, v 0.1 2018Äê12ÔÂ09ÈÕ 2:12 PM rapid Exp $
 */
public class DemoTest {
    @Test
    public void test0() {
        var compiler = new WebAssemblyCompiler();
        var ins = DemoTest.class.getResourceAsStream("/test.wasm");

        var res = compiler.compile(ins);
        System.out.println(res);
    }
}