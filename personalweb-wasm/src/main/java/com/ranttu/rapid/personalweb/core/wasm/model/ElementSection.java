/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;

/**
 * @author rapid
 * @version $Id: ElementSection.java, v 0.1 2018-12-16 5:47 PM rapid Exp $
 */
public class ElementSection extends VectorBasedSection<ElementItem> {
    public ElementSection(ElementItem[] items) {
        super(BinCodes.SCT_ELEMENT, items);
    }
}