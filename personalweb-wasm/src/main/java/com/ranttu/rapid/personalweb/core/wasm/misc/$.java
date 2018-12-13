/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.misc;

import com.ranttu.rapid.personalweb.core.wasm.misc.asm.ClassReader;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.util.TraceClassVisitor;
import lombok.experimental.UtilityClass;

import java.io.PrintWriter;

/**
 * common utilities
 *
 * @author rapid
 * @version $Id: $.java, v 0.1 2018Äê12ÔÂ07ÈÕ 4:41 PM rapid Exp $
 */
@UtilityClass
public class $ {
    /**
     * print the class from byte code
     *
     * @param className the name of the class
     * @param bytes     the bytes
     */
    public void printClass(String className, byte[] bytes) {
        if (Boolean.valueOf(System.getProperty("rapid.printBC"))) {
            System.out.println("========Class: " + className);
            ClassReader reader = new ClassReader(bytes);
            reader.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
            System.out.println();
        }
    }
}