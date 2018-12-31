/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model.raw;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rapid
 * @version $Id: ImportItem.java, v 0.1 2018-12-08- 5:48 PM rapid Exp $
 */
public class ImportItem {
    @Getter
    @Setter
    private String module;

    @Getter
    @Setter
    private String name;

    /**
     * @see com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes#IMP_*
     */
    @Getter
    @Setter
    private byte importType;

    /**
     * only valid for importType == IMP_FUNCTION
     */
    @Getter
    @Setter
    private long typeIdx;

    /**
     * only valid for importType == IMP_TABLE
     */
    @Getter
    @Setter
    private TableType tableType;

    /**
     * only valid for importType == IMP_MEMORY
     */
    @Getter
    @Setter
    private MemoryType memoryType;

    /**
     * only valid for importType == IMP_GLOBAL
     */
    @Getter
    @Setter
    private GlobalType globalType;
}