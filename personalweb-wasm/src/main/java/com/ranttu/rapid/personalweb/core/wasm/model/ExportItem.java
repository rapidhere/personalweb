/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Builder;
import lombok.Getter;

/**
 * @author rapid
 * @version $Id: ExportItem.java, v 0.1 2018Äê12ÔÂ09ÈÕ 4:01 PM rapid Exp $
 */
@Builder
public class ExportItem {
    @Getter
    private final String name;

    /**
     * @see com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes#EXP_*
     */
    @Getter
    private final byte exportType;

    /**
     * depends on export type can be functionIndex/tableIndex/memoryIndex/gloablIndex
     */
    @Getter
    private final long exportIndex;
}