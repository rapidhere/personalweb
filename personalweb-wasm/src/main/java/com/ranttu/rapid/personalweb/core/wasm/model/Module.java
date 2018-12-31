/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.model.raw.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * a web assembly model
 *
 * @author rapid
 * @version $Id: WasmModule.java, v 0.1 2018-12-07- 5:04 PM rapid Exp $
 */
@Getter
@Builder
public class Module {
    /**
     * source for this module
     */
    private final String source;

    private final byte version;

    //~~~ raw info sections
    private final TypeSection typeSection;
    private final ImportSection importSection;
    private final FunctionSection functionSection;
    private final TableSection tableSection;
    private final MemorySection memorySection;
    private final GlobalSection globalSection;
    private final ExportSection exportSection;
    private final CodeSection codeSection;
    private final StartSection startSection;
    private final ElementSection elementSection;

    //~~~ higher level model
    @Setter
    private List<FunctionElement> functions;

    public FunctionElement getFunction(int idx) {
        return functions.get(idx);
    }
}