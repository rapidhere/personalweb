/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import lombok.Builder;
import lombok.Getter;

/**
 * @author rapid
 * @version $Id: WasmInstruction.java, v 0.1 2018-12-08- 10:13 PM rapid Exp $
 */
@Builder
public class Instruction {
    @Getter
    private final byte opcode;

    //~~~ for block typed instructions and if instruction
    @Getter
    private final BlockType blockType;

    @Getter
    private final Instruction[] blockInstructions;

    //~~~ for if instruction
    @Getter
    private final Instruction[] elseInstructions;

    //~~~ for labeled instruction
    @Getter
    private final long labelIndex;

    //~~~ for call instruction
    @Getter
    private final long functionIndex;

    //~~~ for call indirect instruction
    @Getter
    private final long typeIndex;
    @Getter
    private final long tableIndex;

    //~~~ for ldc instructions
    // I32CONST, I64CONST
    @Getter
    private final long intConst;
    // F32CONST, F64CONST
    @Getter
    private final double floatConst;

    //~~~ for local instructions
    @Getter
    private final long localIndex;

    //~~~ for global instructions
    @Getter
    private final long globalIndex;
}