/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: FunctionSection.java, v 0.1 2018-12-08- 7:21 PM rapid Exp $
 */
public class FunctionSection extends VectorBasedSection<Long> {
    public FunctionSection(Long[] items) {
        super(BinCodes.SCT_FUNCTION, items);
    }
}