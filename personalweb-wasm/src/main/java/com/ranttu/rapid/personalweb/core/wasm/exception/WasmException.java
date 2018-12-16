/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.exception;

import lombok.Getter;

/**
 * base wasm exception
 *
 * @author rapid
 * @version $Id: WasmException.java, v 0.1 2018-12-08- 2:09 PM rapid Exp $
 */
public class WasmException extends RuntimeException {
    /**
     * error code
     */
    @Getter
    private String errCode;

    protected WasmException(String errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }

    protected WasmException(String errCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errCode = errCode;
    }
}