/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * a type section
 *
 * @author rapid
 * @version $Id: TypeSection.java, v 0.1 2018-12-08- 3:54 PM rapid Exp $
 */
public class TypeSection extends VectorBasedSection<FunctionType> {
    public TypeSection(FunctionType[] items) {
        super(BinCodes.SCT_TYPE, items);
    }
}