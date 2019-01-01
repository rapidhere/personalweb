/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.test;

import com.ranttu.rapid.personalweb.core.wasm.compile.export.J2WExporter;
import lombok.experimental.var;
import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: J2WExporterTest.java, v 0.1 2018-12-15 8:59 PM rapid Exp $
 */
public class J2WExporterDemoTest {
    @Test
    public void test0() {
        var exporter = new J2WExporter();

        System.out.println(exporter.getTsString());
    }
}