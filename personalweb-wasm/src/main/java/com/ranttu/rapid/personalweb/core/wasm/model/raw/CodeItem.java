/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import lombok.Builder;
import lombok.Getter;

/**
 * @author rapid
 * @version $Id: CodeItem.java, v 0.1 2018-12-09- 4:13 PM rapid Exp $
 */
@Builder
public class CodeItem {
    @Getter
    private final ValueType[] locals;
    @Getter
    private final long[] localCounts;

    @Getter
    private final Instruction[] instructions;
}