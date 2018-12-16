/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: GlobalSection.java, v 0.1 2018-12-08- 7:49 PM rapid Exp $
 */
public class GlobalSection extends VectorBasedSection<GlobalItem> {
    public GlobalSection(GlobalItem[] items) {
        super(BinCodes.SCT_GLOBAL, items);
    }
}