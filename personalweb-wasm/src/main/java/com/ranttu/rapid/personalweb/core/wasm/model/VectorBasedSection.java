/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import java.util.stream.Stream;

/**
 * @author rapid
 * @version $Id: WasmVectorBasedSection.java, v 0.1 2018-12-08- 5:51 PM rapid Exp $
 */
public class VectorBasedSection<T> extends Section {
    /**
     * content of this section
     */
    protected T[] items;

    public VectorBasedSection(byte sectionId, T[] items) {
        super(sectionId);
        this.items = items;
    }

    public int getSize() {
        return items.length;
    }

    public T get(int i) {
        return items[i];
    }

    public Stream<T> stream() {
        return Stream.of(items);
    }
}