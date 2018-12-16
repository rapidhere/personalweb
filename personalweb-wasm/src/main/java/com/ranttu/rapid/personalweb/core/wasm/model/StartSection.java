/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import lombok.Getter;

/**
 * @author rapid
 * @version $Id: StartSection.java, v 0.1 2018-12-16 5:44 PM rapid Exp $
 */
public class StartSection extends Section {
    public StartSection(long funcIndex) {
        super(BinCodes.SCT_START);
        this.funcIndex = funcIndex;
    }

    @Getter
    private final long funcIndex;
}