/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.std;

import com.ranttu.rapid.personalweb.core.wasm.compile.export.Export;
import com.ranttu.rapid.personalweb.core.wasm.compile.export.J2WExport;

/**
 * java language support
 *
 * @author rapid
 * @version $Id: JavaLang.java, v 0.1 2018Äê12ÔÂ15ÈÕ 8:13 PM rapid Exp $
 */
@J2WExport(exportStatic = true, namespace = "Math")
final public class MathSupports {
    private MathSupports() {
    }

    @Export
    public static double sqrt(double a) {
        return Math.sqrt(a);
    }

    @Export
    public static long powMod(long a, long n, long m) {
        // TODO: parameter check
        return powMod0(a, n, m);
    }

    private static long powMod0(long a, long n, long m) {
        if (m == 0) {
            return 1;
        } else if (m == 1) {
            return a % m;
        } else {
            long r0 = powMod0(a, n >> 1, m);

            if ((m & 1) == 1) {
                return (((r0 * r0) % m) * a) % m;
            } else {
                return (r0 * r0) % m;
            }
        }
    }
}