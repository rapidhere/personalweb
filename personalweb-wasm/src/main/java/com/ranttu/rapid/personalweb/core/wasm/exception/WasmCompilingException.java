/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.exception;

/**
 * compiling exception
 * @author rapid
 * @version $Id: WasmCompilingException.java, v 0.1 2018-12-08- 2:11 PM rapid Exp $
 */
public class WasmCompilingException extends WasmException {
    public WasmCompilingException(String errCode, String msg) {
        super(errCode, msg);
    }

    public WasmCompilingException(String errCode, String msg, Throwable cause) {
        super(errCode, msg, cause);
    }
}