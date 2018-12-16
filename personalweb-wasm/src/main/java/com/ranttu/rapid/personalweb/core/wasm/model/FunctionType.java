/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import lombok.Getter;
import lombok.Setter;

/**
 * @author rapid
 * @version $Id: FunctionType.java, v 0.1 2018-12-08- 5:24 PM rapid Exp $
 */
public class FunctionType {
    @Getter
    private final byte functionCode = BinCodes.FUN_PREFIX;

    @Getter
    @Setter
    private ValueType[] parameters;

    @Setter
    @Getter
    private ValueType result;

    public int getParameterSize() {
        return parameters.length;
    }

    public ValueType getParameter(int index) {
        return parameters[index];
    }

    public boolean hasResult() {
        return result != null;
    }
}