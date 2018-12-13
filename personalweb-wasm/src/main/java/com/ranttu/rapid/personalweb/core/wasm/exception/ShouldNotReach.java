/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.exception;

import com.ranttu.rapid.personalweb.core.wasm.constants.ErrorCodes;

/**
 * @author rapid
 * @version $Id: ShouldNotReach.java, v 0.1 2018Äê12ÔÂ14ÈÕ 12:52 AM rapid Exp $
 */
public class ShouldNotReach extends WasmException {
    public ShouldNotReach() {
        super(ErrorCodes.UNKNOWN_ERROR, "should not reach here");
    }
}