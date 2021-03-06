/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import com.ranttu.rapid.personalweb.core.wasm.model.raw.Instruction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.var;

/**
 * @author rapid
 * @version $Id: InstructionElement.java, v 0.1 2018-12-30 10:32 PM rapid Exp $
 */
@Getter
@Setter
public class InstructionElement {
    private Instruction instruction;
    private FunctionElement functionElement;
    private Module module;

    public short getOpcode() {
        return instruction.getOpcode();
    }

    public TypeElement getLocalType() {
        var localIdx = getLocalIndex();
        if (localIdx < functionElement.getParameterSize()) {
            return functionElement.getParameterTypes().get(localIdx);
        } else {
            return functionElement.getLocalTypes().get(
                localIdx - functionElement.getParameterSize());
        }
    }

    public Object getConst() {
        switch (getOpcode()) {
            case BinCodes.OP_I32CONST:
                return (int) instruction.getIntConst();
            case BinCodes.OP_I64CONST:
                return instruction.getIntConst();
            default:
                return instruction.getFloatConst();
        }
    }

    public int getLocalIndex() {
        return (int) instruction.getLocalIndex();
    }

    public FunctionElement getCallFunction() {
        return module.getFunction((int) instruction.getFunctionIndex());
    }
}