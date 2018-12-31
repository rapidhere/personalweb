/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import com.ranttu.rapid.personalweb.core.wasm.exception.ShouldNotReach;
import com.ranttu.rapid.personalweb.core.wasm.model.*;
import com.ranttu.rapid.personalweb.core.wasm.model.raw.CodeItem;
import com.ranttu.rapid.personalweb.core.wasm.model.raw.Instruction;
import com.ranttu.rapid.personalweb.core.wasm.model.raw.ValueType;
import lombok.SneakyThrows;
import lombok.experimental.var;

import java.lang.reflect.Method;
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

        composeImports(module);
        composeFunctions(module);
        composeExports(module);
    }

    private void composeImports(Module module) {
        if (module.getImportSection() == null) {
            return;
        }

        module.getImportSection().stream().forEach(importItem -> {
            if (importItem.getImportType() == BinCodes.IMP_FUNCTION) {
                FunctionElement func = composeFunction(
                    module,
                    (int) importItem.getTypeIdx(),
                    -1,
                    importItem.getName()
                );
                func.setImported(true);
                func.setImportModule(importItem.getModule());

                if (func.isStaticImport()) {
                    func.setDelegateMethod(findDelegateMethod(importItem.getModule(), importItem.getName()));
                } else if (func.isClassImport()) {
                    String[] tmp = importItem.getName().split("#");
                    func.setDelegateMethod(findDelegateMethod(tmp[0].replace('_', '.'), tmp[1]));
                } else {
                    throw new ShouldNotReach();
                }

                module.getFunctions().add(func);
            }
            // TODO: others are ignored
        });
    }

    @SneakyThrows
    private Method findDelegateMethod(String module, String methodName) {
        return Stream.of(Class.forName(module).getMethods())
            .filter(m -> m.getName().equals(methodName))
            .findFirst()
            .orElse(null);
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
            module.getFunctions().add(composeFunction(
                module,
                module.getFunctionSection().get(funIdx).intValue(),
                funIdx,
                "$" + funIdx
            ));
        }
    }

    private FunctionElement composeFunction(Module module, int typeIdx, int codeIdx, String name) {
        CodeItem codeItem = null;
        if (codeIdx >= 0) {
            codeItem = module.getCodeSection().get(codeIdx);
        }

        var typeItem = module.getTypeSection().get(typeIdx);

        var funcEle = new FunctionElement();
        funcEle.setIndex(module.getFunctions().size());

        // default name
        funcEle.setName(name);

        // parameters and result
        funcEle.setResultType(composeTypeElement(typeItem.getResult()));
        Stream.of(typeItem.getParameters()).forEach(
            t -> funcEle.getParameterTypes().add(composeTypeElement(t)));

        if (codeItem != null) {
            // local variables
            for (int ii = 0; ii < codeItem.getLocals().length; ii++) {
                for (int j = 0; j < codeItem.getLocalCounts()[ii]; j++) {
                    funcEle.getLocalTypes().add(composeTypeElement(codeItem.getLocals()[ii]));
                }
            }

            // instructions
            Stream.of(codeItem.getInstructions()).forEach(
                c -> funcEle.getInstructions().add(composeInstructionElement(funcEle, c, module)));
        }

        return funcEle;
    }

    private InstructionElement composeInstructionElement(FunctionElement functionElement, Instruction instruction, Module module) {
        var instructionElement = new InstructionElement();
        instructionElement.setInstruction(instruction);
        instructionElement.setFunctionElement(functionElement);
        instructionElement.setModule(module);

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