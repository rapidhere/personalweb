/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * a type
 *
 * @author rapid
 * @version $Id: TypeElement.java, v 0.1 2018-12-30 10:29 PM rapid Exp $
 */
@Getter
@Setter
public class TypeElement {
    private boolean generic;
    private short rawType;
    private String javaTypeStr;
    private String javaTypeDesc;
    private short javaTypeSize;

    private List<TypeElement> typeParameters;

    public static final TypeElement VOID_TYPE = new TypeElement();
    public static final TypeElement I32_TYPE = new TypeElement();
    public static final TypeElement I64_TYPE = new TypeElement();
    public static final TypeElement F32_TYPE = new TypeElement();
    public static final TypeElement F64_TYPE = new TypeElement();

    static {
        VOID_TYPE.javaTypeStr = "void";
        VOID_TYPE.javaTypeDesc = "V";
        VOID_TYPE.javaTypeSize = 4;

        I32_TYPE.rawType = BinCodes.VAL_I32;
        I32_TYPE.javaTypeStr = "int";
        I32_TYPE.javaTypeDesc = "I";
        I32_TYPE.javaTypeSize = 4;

        I64_TYPE.rawType = BinCodes.VAL_I64;
        I64_TYPE.javaTypeStr = "long";
        I64_TYPE.javaTypeDesc = "J";
        I64_TYPE.javaTypeSize = 8;

        F32_TYPE.rawType = BinCodes.VAL_F32;
        F32_TYPE.javaTypeStr = "float";
        F32_TYPE.javaTypeDesc = "F";
        F32_TYPE.javaTypeSize = 4;

        F64_TYPE.rawType = BinCodes.VAL_F64;
        F64_TYPE.javaTypeStr = "double";
        F64_TYPE.javaTypeDesc = "D";
        F64_TYPE.javaTypeSize = 8;
    }
}