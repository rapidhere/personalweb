/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.test.unit;

import com.ranttu.rapid.personalweb.core.wasm.test.BaseWasmUnitTest;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: Add.java, v 0.1 2018年12月15日 7:07 PM rapid Exp $
 */
public class Add extends BaseWasmUnitTest {
    @Test
    public void test() {
        Assert.assertEquals(callFunc("add", 1, 2), 3);
        Assert.assertEquals(callFunc("add", -1, -1), -2);
    }
}