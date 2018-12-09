/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

/**
 * @author rapid
 * @version $Id: WasmVectorBasedSection.java, v 0.1 2018Äê12ÔÂ08ÈÕ 5:51 PM rapid Exp $
 */
public class WasmVectorBasedSection<T> extends WasmSection {
    /** content of this section */
    protected T[] items;

    public WasmVectorBasedSection(byte sectionId, T[] items) {
        super(sectionId);
        this.items = items;
    }

    public int getSize() {
        return items.length;
    }

    public T get(int i) {
        return items[i];
    }
}