/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.exception;

import com.ranttu.rapid.personalweb.core.wasm.constants.ErrorCodes;

/**
 * @author rapid
 * @version $Id: UnexpectedEOF.java, v 0.1 2018Äê12ÔÂ08ÈÕ 2:12 PM rapid Exp $
 */
public class UnexpectedEOF extends WasmCompilingException {
    public UnexpectedEOF(String msg) {
        super(ErrorCodes.UNEXPECTED_EOF, msg);
    }

    public UnexpectedEOF(String msg, Throwable cause) {
        super(ErrorCodes.UNEXPECTED_EOF, msg, cause);
    }
}