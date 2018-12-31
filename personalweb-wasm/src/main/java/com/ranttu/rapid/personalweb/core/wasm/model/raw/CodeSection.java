/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: CodeSection.java, v 0.1 2018-12-09- 4:13 PM rapid Exp $
 */
public class CodeSection extends VectorBasedSection<CodeItem> {
    public CodeSection(CodeItem[] items) {
        super(BinCodes.SCT_CODE, items);
    }
}