/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile.export;

import java.lang.annotation.*;

/**
 * @author rapid
 * @version $Id: ExportItem.java, v 0.1 2018-12-15- 8:17 PM rapid Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Export {
    /**
     * just a marker
     */
    boolean buildIn() default false;
}