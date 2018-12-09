/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: ExportSection.java, v 0.1 2018Äê12ÔÂ09ÈÕ 4:01 PM rapid Exp $
 */
public class ExportSection extends WasmVectorBasedSection<ExportItem> {
    public ExportSection(ExportItem[] items) {
        super(BinCodes.SCT_EXPORT, items);
    }
}