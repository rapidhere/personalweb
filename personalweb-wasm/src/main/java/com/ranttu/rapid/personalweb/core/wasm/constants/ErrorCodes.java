/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.constants;

/**
 * error code constant
 *
 * @author rapid
 * @version $Id: ErrorCodes.java, v 0.1 2018-12-08- 2:12 PM rapid Exp $
 */
public interface ErrorCodes {
    //~~~ begin with 00, compiling error
    String UNEXPECTED_EOF = "00001";

    String UNKNOWN_SECTION = "00002";

    String UNEXPECTED_BYTE = "00003";

    String UNSUPPORTED_FEATURE = "00004";

    String UNKNOWN_VALUE_TYPE = "00005";

    String UNKNOWN_IMPORT_TYPE = "00006";

    String UNKNOWN_LIMIT_TYPE = "00007";

    String UNKNOWN_OPCODE = "00008";

    String UNSUPPORTED_VERSION = "00009";

    String MAGICNUMBER_CHECK_FAILED = "00010";

    String UNKNOWN_EXPORT_TYPE = "00011";

    //~~~ unknown error
    String UNKNOWN_ERROR = "99999";
}