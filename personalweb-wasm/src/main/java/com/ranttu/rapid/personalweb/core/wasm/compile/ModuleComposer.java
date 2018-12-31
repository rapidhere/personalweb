/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import com.ranttu.rapid.personalweb.core.wasm.exception.ShouldNotReach;
import com.ranttu.rapid.personalweb.core.wasm.model.*;
import com.ranttu.rapid.personalweb.core.wasm.model.raw.Instruction;
import com.ranttu.rapid.personalweb.core.wasm.model.raw.ValueType;
import lombok.experimental.var;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * compose higher info for a wasm module
 *
 * @author rapid
 * @version $Id: ModuleComposer.java, v 0.1 2018-12-31 4:10 PM rapid Exp $
 */
public class ModuleComposer {
    public static void compose(Module module) {
        new ModuleComposer().doCompose(module);
    }

    //~~~ impl
    private void doCompose(Module module) {
        // init
        module.setFunctions(new ArrayList<>());

        composeFunctions(module);
        composeExports(module);
    }

    private void composeExports(Module module) {
        module.getExportSection().stream().forEach(exportItem -> {
            ExposableElement ele = null;
            if (exportItem.getExportType() == BinCodes.EXP_FUNCTION) {
                ele = module.getFunction((int) exportItem.getExportIndex());
            }
            // TODO: others are ignored

            if (ele != null) {
                ele.setExported(true);
                ele.setName(exportItem.getName());
            }
        });
    }

    private void composeFunctions(Module module) {
        for (int funIdx = 0; funIdx < module.getFunctionSection().getSize(); funIdx++) {
            var codeItem = module.getCodeSection().get(funIdx);
            var typeIdx = module.getFunctionSection().get(funIdx).intValue();
            var typeItem = module.getTypeSection().get(typeIdx);

            var funcEle = new FunctionElement();
            funcEle.setIndex(funIdx);

            // default name
            funcEle.setName("$" + funIdx);

            // parameters and result
            funcEle.setResultType(composeTypeElement(typeItem.getResult()));
            Stream.of(typeItem.getParameters()).forEach(
                t -> funcEle.getParameterTypes().add(composeTypeElement(t)));

            // local variables
            for (int i = 0; i < codeItem.getLocals().length; i++) {
                for (int j = 0; j < codeItem.getLocalCounts()[i]; j++) {
                    funcEle.getLocalTypes().add(composeTypeElement(codeItem.getLocals()[i]));
                }
            }

            // instructions
            Stream.of(codeItem.getInstructions()).forEach(
                i -> funcEle.getInstructions().add(composeInstructionElement(funcEle, i)));

            module.getFunctions().add(funcEle);
        }
    }

    private InstructionElement composeInstructionElement(FunctionElement functionElement, Instruction instruction) {
        var instructionElement = new InstructionElement();
        instructionElement.setInstruction(instruction);
        instructionElement.setFunctionElement(functionElement);

        return instructionElement;
    }

    private TypeElement composeTypeElement(ValueType valueType) {
        if (valueType == null) {
            return TypeElement.VOID_TYPE;
        } else {
            switch (valueType.getTypeCode()) {
                case BinCodes.VAL_I32:
                    return TypeElement.I32_TYPE;
                case BinCodes.VAL_I64:
                    return TypeElement.I64_TYPE;
                case BinCodes.VAL_F32:
                    return TypeElement.F32_TYPE;
                case BinCodes.VAL_F64:
                    return TypeElement.F64_TYPE;
                default:
                    throw new ShouldNotReach();
            }
        }
    }
}