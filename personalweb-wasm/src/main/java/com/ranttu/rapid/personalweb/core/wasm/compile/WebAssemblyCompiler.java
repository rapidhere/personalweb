/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import com.ranttu.rapid.personalweb.core.wasm.constants.ErrorCodes;
import com.ranttu.rapid.personalweb.core.wasm.exception.ShouldNotReach;
import com.ranttu.rapid.personalweb.core.wasm.exception.WasmCompilingException;
import com.ranttu.rapid.personalweb.core.wasm.misc.$;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.ClassWriter;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.MethodVisitor;
import com.ranttu.rapid.personalweb.core.wasm.model.*;
import com.ranttu.rapid.personalweb.core.wasm.model.runtime.WasmModule;
import lombok.experimental.var;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.ranttu.rapid.personalweb.core.wasm.misc.asm.Opcodes.*;
import static com.ranttu.rapid.personalweb.core.wasm.misc.asm.Type.getInternalName;

/**
 * compiler for wasm
 * only compile a simple module
 *
 * @author rapid
 * @version $Id: WebAssemblyCompiler.java, v 0.1 2018Äê12ÔÂ07ÈÕ 5:00 PM rapid Exp $
 */
public class WebAssemblyCompiler {
    /**
     * module parser
     */
    private WasmParser wasmParser = new WasmParser();

    /**
     * module validator
     */
    private WasmModuleValidator wasmValidator = new WasmModuleValidator();

    /**
     * module class definer
     */
    private final WasmModuleClassLoader wasmModuleClassLoader = new WasmModuleClassLoader();

    /**
     * counter of created classes
     */
    private final AtomicInteger classCounter = new AtomicInteger(0);

    /**
     * get the compiled wasm module
     * NOTE: compiler will close the stream
     */
    public WasmModule compile(InputStream inputStream) {
        // parse source
        var module = wasmParser.parseModule(inputStream);

        // validate
        wasmValidator.validateModule(module);

        // compile
        return doCompile(module);
    }

    private WasmModule doCompile(Module module) {
        var className = genClzName();
        var bytes = assembleModuleClass(module, className);
        $.printClass(className, bytes);

        try {
            // define class and return module instance
            Class<?> c = wasmModuleClassLoader.defineClass(className, bytes);
            return (WasmModule) c.newInstance();
        } catch (Throwable e) {
            throw new WasmCompilingException(
                ErrorCodes.UNKNOWN_ERROR, "failed to create module class", e);
        }
    }

    private byte[] assembleModuleClass(Module module, String className) {
        var internalClassName = className.replace('.', '/');
        var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_6,
            ACC_SYNTHETIC | ACC_SUPER | ACC_PUBLIC,
            internalClassName,
            null,
            getInternalName(WasmModule.class),
            new String[0]);
        cw.visitSource("<wasm>", null);

        assembleConstructor(module, cw);
        assembleFunctions(module, cw);

        cw.visitEnd();
        return cw.toByteArray();
    }

    private void assembleConstructor(@SuppressWarnings("unused") Module module, ClassWriter cw) {
        var mv = cw.visitMethod(
            ACC_PUBLIC,
            "<init>",
            "()V",
            null,
            new String[0]
        );

        // call super
        // TODO: support constructor arguments
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL,
            getInternalName(WasmModule.class),
            "<init>", "()V", false);
        mv.visitInsn(RETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void assembleFunctions(Module module, ClassWriter cw) {
        for (int funIdx = 0; funIdx < module.getFunctionSection().getSize(); funIdx++) {
            var codeItem = module.getCodeSection().get(funIdx);
            var exportItem = module.getExportSection().findItem(BinCodes.EXP_FUNCTION, funIdx);
            var typeIdx = module.getFunctionSection().get(funIdx).intValue();
            var typeItem = module.getTypeSection().get(typeIdx);

            String funcName;
            int accFlag;
            // exported function
            if (exportItem != null) {
                // TODO: not a valid java name
                funcName = exportItem.getName();
                accFlag = ACC_PUBLIC;
            }
            // not exported
            else {
                funcName = "$" + funIdx;
                accFlag = ACC_PRIVATE;
            }

            var mv = cw.visitMethod(
                accFlag,
                funcName,
                genFunSig(typeItem),
                null,
                new String[0]
            );

            // visit codes
            mv.visitCode();
            Stream.of(codeItem.getInstructions())
                .forEach(instruction -> assembleInstruction(mv, typeItem, codeItem, instruction));

            // TODO: return instruction
            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
    }

    private void assembleInstruction(MethodVisitor mv, FunctionType funType, CodeItem codeItem, Instruction instruction) {
        switch (instruction.getOpcode()) {
            case BinCodes.OP_LOCALGET: {
                int localIdx = (int) instruction.getLocalIndex();
                assembleLocalGet(mv, localIdx, getLocalType(funType, codeItem, localIdx));
                break;
            }
            case BinCodes.OP_I32ADD: {
                mv.visitInsn(IADD);
                break;
            }
            default:
                throw new WasmCompilingException(
                    ErrorCodes.UNKNOWN_OPCODE, "" + instruction.getOpcode());
        }
    }

    private ValueType getLocalType(FunctionType functionType, CodeItem codeItem, int localIdx) {
        if (localIdx < functionType.getParameterSize()) {
            return functionType.getParameter(localIdx);
        } else {
            localIdx -= functionType.getParameterSize();
            for (int i = 0; i < codeItem.getLocalCounts().length; i++) {
                if (localIdx < codeItem.getLocalCounts()[i]) {
                    return codeItem.getLocals()[i];
                } else {
                    localIdx -= codeItem.getLocalCounts()[i];
                }
            }

            throw new ShouldNotReach();
        }
    }

    private void assembleLocalGet(MethodVisitor mv, int localIdx, ValueType valueType) {
        switch (valueType.getTypeCode()) {
            case BinCodes.VAL_I32:
                mv.visitVarInsn(ILOAD, localIdx + 1);
                break;
            case BinCodes.VAL_F32:
                mv.visitVarInsn(FLOAD, localIdx + 1);
                break;
            case BinCodes.VAL_I64:
                mv.visitVarInsn(LLOAD, localIdx + 1);
                break;
            case BinCodes.VAL_F64:
                mv.visitVarInsn(DLOAD, localIdx + 1);
                break;
            default:
                throw new ShouldNotReach();
        }
    }

    private String genFunSig(FunctionType functionType) {
        var sb = new StringBuilder(functionType.getParameterSize());

        // parameters
        sb.append('(');
        Stream.of(functionType.getParameters())
            .forEach(valueType -> sb.append(toJavaTypeStr(valueType)));
        sb.append(')');

        // result
        sb.append(toJavaTypeStr(functionType.getResult()));

        return sb.toString();
    }

    private String toJavaTypeStr(ValueType valueType) {
        switch (valueType.getTypeCode()) {
            case BinCodes.VAL_I32:
                return "I";
            case BinCodes.VAL_I64:
                return "J";
            case BinCodes.VAL_F32:
                return "F";
            case BinCodes.VAL_F64:
                return "D";
            default:
                throw new ShouldNotReach();
        }
    }

    private String genClzName() {
        return "com.ranttu.rapid.personalweb.core.wasm.WASM_CompiledStub$" + classCounter.getAndIncrement();
    }

    private class WasmModuleClassLoader extends ClassLoader {
        /**
         * define the class
         */
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }

    }
}