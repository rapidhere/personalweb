/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.test;

import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: DemoTest.java, v 0.1 2018-12-09 2:12 PM rapid Exp $
 */
public class DemoTest {
    private int c;

    @Test
    public void test0() {
        new Thread(() -> System.out.print("hello world"));
    }
}