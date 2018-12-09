/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * a wasm section
 * @author rapid
 * @version $Id: WasmSection.java, v 0.1 2018Äê12ÔÂ08ÈÕ 3:50 PM rapid Exp $
 */
@RequiredArgsConstructor
abstract public class WasmSection {
    @Getter
    private final byte sectionId;
}