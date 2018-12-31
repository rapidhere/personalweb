/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.misc;

import com.ranttu.rapid.personalweb.core.wasm.exception.ShouldNotReach;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.ClassReader;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.util.TraceClassVisitor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import java.io.PrintWriter;
import java.lang.reflect.Field;

/**
 * common utilities
 *
 * @author rapid
 * @version $Id: $.java, v 0.1 2018-12-07- 4:41 PM rapid Exp $
 */
@UtilityClass
public class $ {
    private final Unsafe unsafe = initUnsafe();

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

    public <T> T should(boolean condition) {
        if (!condition) {
            throw new ShouldNotReach();
        }

        return null;
    }

    public Unsafe unsafe() {
        return unsafe;
    }

    @SneakyThrows
    private Unsafe initUnsafe() {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafe = null;
        Field[] fields = unsafeClass.getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().equals("theUnsafe")) {
                theUnsafe = field;
            }
        }

        if (theUnsafe != null) {
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        }

        return null;
    }
}