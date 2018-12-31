/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile.export;

import java.lang.annotation.*;

/**
 * @author rapid
 * @version $Id: J2WExport.java, v 0.1 2018-12-15- 8:13 PM rapid Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface J2WExport {
    boolean exportStatic() default false;

    // for static export only
    String namespace() default "";

    // for class export only
    String typeAlias() default "";
}