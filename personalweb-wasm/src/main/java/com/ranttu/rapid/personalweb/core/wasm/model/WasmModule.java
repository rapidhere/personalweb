/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Builder;
import lombok.Getter;

/**
 * a web assembly model
 *
 * @author rapid
 * @version $Id: WasmModule.java, v 0.1 2018Äê12ÔÂ07ÈÕ 5:04 PM rapid Exp $
 */
@Getter
@Builder
public class WasmModule {
    /**
     * source for this module
     */
    private final String source;

    private final byte version;

    //~~~ sections
    private final TypeSection typeSection;
    private final ImportSection importSection;
    private final FunctionSection functionSection;
    private final TableSection tableSection;
    private final MemorySection memorySection;
    private final GlobalSection globalSection;
    private final ExportSection exportSection;
    private final CodeSection codeSection;
}