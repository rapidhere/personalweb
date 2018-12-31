/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Getter;
import lombok.Setter;

/**
 * eposable element
 *
 * @author rapid
 * @version $Id: ExposableElement.java, v 0.1 2018-12-30 10:02 PM rapid Exp $
 */
@Getter
@Setter
abstract public class ExposableElement {
    /**
     * is this element export
     */
    protected boolean exported;

    /**
     * is this element import from outer
     */
    protected boolean imported;

    /**
     * name of this element
     */
    protected String name;

    /**
     * exported index
     */
    protected int index;
}