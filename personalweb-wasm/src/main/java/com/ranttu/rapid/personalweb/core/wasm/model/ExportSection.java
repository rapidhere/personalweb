/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: ExportSection.java, v 0.1 2018-12-09- 4:01 PM rapid Exp $
 */
public class ExportSection extends VectorBasedSection<ExportItem> {
    public ExportSection(ExportItem[] items) {
        super(BinCodes.SCT_EXPORT, items);
    }

    public ExportItem findItem(byte exportType, long exportIndex) {
        return stream()
            .filter(item -> item.getExportType() == exportType && item.getExportIndex() == exportIndex)
            .findAny()
            .orElse(null);
    }
}