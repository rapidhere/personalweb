/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: MemorySection.java, v 0.1 2018-12-08- 7:39 PM rapid Exp $
 */
public class MemorySection extends VectorBasedSection<MemoryType> {
    public MemorySection(MemoryType[] items) {
        super(BinCodes.SCT_MEMORY, items);
    }
}