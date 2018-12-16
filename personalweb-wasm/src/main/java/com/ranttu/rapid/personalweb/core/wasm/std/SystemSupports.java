/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.std;

import com.ranttu.rapid.personalweb.core.wasm.compile.export.Export;
import com.ranttu.rapid.personalweb.core.wasm.compile.export.J2WExport;

/**
 * @author rapid
 * @version $Id: LangSupports.java, v 0.1 2018-12-16 5:06 PM rapid Exp $
 */
@J2WExport(exportStatic = true, namespace = "rt")
public class SystemSupports {
    @Export
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Export
    public static long currentTimeNanos() {
        return System.nanoTime();
    }

    @Export
    public static void exit(int status) {
        System.exit(status);
    }
}