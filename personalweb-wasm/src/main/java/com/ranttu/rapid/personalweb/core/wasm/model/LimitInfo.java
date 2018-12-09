/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author rapid
 * @version $Id: LimitInfo.java, v 0.1 2018Äê12ÔÂ08ÈÕ 6:26 PM rapid Exp $
 */
@RequiredArgsConstructor
public class LimitInfo {
    @Getter
    private final byte limitType;

    @Getter
    private final long minimum;

    @Getter
    private final long maximum;
}