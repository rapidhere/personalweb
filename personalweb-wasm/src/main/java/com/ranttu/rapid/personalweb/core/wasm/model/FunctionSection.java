/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: FunctionSection.java, v 0.1 2018Äê12ÔÂ08ÈÕ 7:21 PM rapid Exp $
 */
public class FunctionSection extends WasmVectorBasedSection<Long> {
    public FunctionSection(Long[] items) {
        super(BinCodes.SCT_FUNCTION, items);
    }
}