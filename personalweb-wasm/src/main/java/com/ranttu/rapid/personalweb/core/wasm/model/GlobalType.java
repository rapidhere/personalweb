/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rapid
 * @version $Id: GlobalType.java, v 0.1 2018Äê12ÔÂ08ÈÕ 7:15 PM rapid Exp $
 */
@RequiredArgsConstructor
public class GlobalType {
    @Getter
    private final ValueType valueType;

    @Getter
    private final byte mutFlag;

    public boolean isConstant() {
        return mutFlag == BinCodes.GLB_CONST;
    }
}