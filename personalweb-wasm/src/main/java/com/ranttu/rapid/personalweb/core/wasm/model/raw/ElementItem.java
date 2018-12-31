/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import lombok.Builder;
import lombok.Getter;

/**
 * @author rapid
 * @version $Id: ElementItem.java, v 0.1 2018-12-16 5:48 PM rapid Exp $
 */
@Builder
public class ElementItem {
    @Getter
    private final long tableIndex;

    @Getter
    private final Instruction[] instructions;

    @Getter
    private final long[] funcIndexes;
}