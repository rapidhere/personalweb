/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes.TBL_ELEMENT;

/**
 * @author rapid
 * @version $Id: TableType.java, v 0.1 2018-12-08- 6:22 PM rapid Exp $
 */
@RequiredArgsConstructor
public class TableType {
    // element type is forever 0x70 currently
    private final byte elementType = TBL_ELEMENT;

    @Getter
    private final LimitInfo limitInfo;
}