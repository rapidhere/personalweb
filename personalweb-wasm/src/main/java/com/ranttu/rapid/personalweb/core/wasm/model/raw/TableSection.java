/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: TableSection.java, v 0.1 2018-12-08 7:34 PM rapid Exp $
 */
public class TableSection extends VectorBasedSection<TableType> {
    public TableSection(TableType[] items) {
        super(BinCodes.SCT_TABLE, items);
    }
}