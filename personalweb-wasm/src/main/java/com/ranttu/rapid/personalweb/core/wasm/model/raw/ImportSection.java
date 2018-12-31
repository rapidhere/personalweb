/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: ImportSection.java, v 0.1 2018-12-08- 5:46 PM rapid Exp $
 */
public class ImportSection extends VectorBasedSection<ImportItem> {
    public ImportSection(ImportItem[] items) {
        super(BinCodes.SCT_IMPORT, items);
    }
}