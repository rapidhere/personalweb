/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.exception;

import com.ranttu.rapid.personalweb.core.wasm.constants.ErrorCodes;

/**
 * @author rapid
 * @version $Id: WasmUnknownError.java, v 0.1 2018Äê12ÔÂ08ÈÕ 2:14 PM rapid Exp $
 */
public class WasmUnknownError extends WasmException {
    public WasmUnknownError(String msg) {
        super(ErrorCodes.UNKNOWN_ERROR, msg);
    }

    public WasmUnknownError(String msg, Throwable cause) {
        super(ErrorCodes.UNKNOWN_ERROR, msg, cause);
    }
}