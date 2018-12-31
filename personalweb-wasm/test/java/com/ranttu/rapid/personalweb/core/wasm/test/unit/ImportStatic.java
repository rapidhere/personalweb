/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.test.unit;

import com.ranttu.rapid.personalweb.core.wasm.test.BaseWasmUnitTest;
import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: ImportStatic.java, v 0.1 2018-12-31 6:14 PM rapid Exp $
 */
public class ImportStatic extends BaseWasmUnitTest {
    @Test
    public void test0() {
        System.out.println(callFunc("random", 10));
    }
}