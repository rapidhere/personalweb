/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.test;

import com.ranttu.rapid.personalweb.core.wasm.compile.export.J2WExporter;
import com.ranttu.rapid.personalweb.core.wasm.std.MathSupports;
import lombok.experimental.var;
import org.testng.annotations.Test;

/**
 * @author rapid
 * @version $Id: J2WExporterTest.java, v 0.1 2018Äê12ÔÂ15ÈÕ 8:59 PM rapid Exp $
 */
public class J2WExporterDemoTest {
    @Test
    public void test0() {
        var exporter = new J2WExporter();
        exporter.append(MathSupports.class);

        System.out.println(exporter.getTsString());
    }
}