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
 * @version $Id: BlockType.java, v 0.1 2018Äê12ÔÂ09ÈÕ 12:26 PM rapid Exp $
 */
@RequiredArgsConstructor
public class BlockType {
    @Getter
    private final byte blockTypeCode = BinCodes.BLK_PREFIX;

    @Getter
    private final ValueType valueType;
}