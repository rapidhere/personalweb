/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rapid
 * @version $Id: GlobalItem.java, v 0.1 2018-12-08- 7:49 PM rapid Exp $
 */
@RequiredArgsConstructor
public class GlobalItem {
    @Getter
    private final GlobalType globalType;

    @Getter
    private final Instruction[] instructions;
}