/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.rt;

import com.ranttu.rapid.personalweb.core.wasm.compile.export.Export;
import com.ranttu.rapid.personalweb.core.wasm.compile.export.J2WExport;

/**
 * an wasm module, for compile usage
 * @author rapid
 * @version $Id: WasmModule.java, v 0.1 2018-12-11- 10:27 PM rapid Exp $
 */
@J2WExport(typeAlias = "WasmModule")
public abstract class WasmModule {
    @Export
    abstract public String getSourceName();

    @Export
    abstract public String getMetaVersion();
}