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
import com.ranttu.rapid.personalweb.core.wasm.model.InstructionElement;
import com.ranttu.rapid.personalweb.core.wasm.model.Module;
import com.ranttu.rapid.personalweb.core.wasm.model.TypeElement;
import com.ranttu.rapid.personalweb.core.wasm.model.runtime.WasmModule;
import lombok.experimental.var;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ranttu.rapid.personalweb.core.wasm.misc.asm.Opcodes.*;
import static com.ranttu.rapid.personalweb.core.wasm.misc.asm.Type.getInternalName;

/**
 * compiler for wasm
 * only compile a simple module
 *
 * @author rapid
 * @version $Id: WebAssemblyCompiler.java, v 0.1 2018-12-07 5:00 PM rapid Exp $
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
        var ctx = new CompilingContext();
        ctx.className = genClzName();
        ctx.module = module;

        var bytes = assembleModuleClass(ctx);
        $.printClass(ctx.className, bytes);

        try {
            // define class and return module instance
            Class<?> c = wasmModuleClassLoader.defineClass(ctx.className, bytes);
            return (WasmModule) c.newInstance();
        } catch (Throwable e) {
            throw new WasmCompilingException(
                ErrorCodes.UNKNOWN_ERROR, "failed to create module class", e);
        }
    }

    private byte[] assembleModuleClass(CompilingContext ctx) {
        // set internal class name
        ctx.internalClassName = ctx.className.replace('.', '/');

        var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_6,
            ACC_SYNTHETIC | ACC_SUPER | ACC_PUBLIC,
            ctx.internalClassName,
            null,
            getInternalName(WasmModule.class),
            new String[0]);
        cw.visitSource("<wasm>", null);

        assembleConstructor(cw, ctx);
        assembleFunctions(cw, ctx);

        cw.visitEnd();
        return cw.toByteArray();
    }

    private void assembleConstructor(ClassWriter cw, @SuppressWarnings("unused") CompilingContext ctx) {
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

    private void assembleFunctions(ClassWriter cw, CompilingContext ctx) {
        var module = ctx.module;

        module.getFunctions().forEach(func -> {
            var mv = cw.visitMethod(
                func.isExported() ? ACC_PUBLIC : ACC_PRIVATE,
                func.getName(),
                func.getDesc(),
                null,
                new String[0]
            );

            // visit codes
            mv.visitCode();
            func.getInstructions().forEach(instructionElement ->
                assembleInstruction(mv, instructionElement));

            // TODO: return instruction
            mv.visitInsn(IRETURN);

            mv.visitMaxs(0, 0);
            mv.visitEnd();
        });
    }

    private void assembleInstruction(MethodVisitor mv, InstructionElement instruction) {
        switch (instruction.getOpcode()) {
            case BinCodes.OP_LOCALGET: {
                assembleLocalGet(mv, instruction.getLocalIndex(), instruction.getLocalType());
                break;
            }
            case BinCodes.OP_I32ADD: {
                mv.visitInsn(IADD);
                break;
            }
            case BinCodes.OP_I64ADD: {
                mv.visitInsn(LADD);
                break;
            }
            case BinCodes.OP_CALL: {
                break;
            }
            case BinCodes.OP_NOP: {
                mv.visitInsn(NOP);
                break;
            }
            default:
                throw new WasmCompilingException(
                    ErrorCodes.UNKNOWN_OPCODE, "" + instruction.getOpcode());
        }
    }

    private void assembleLocalGet(MethodVisitor mv, int localIdx, TypeElement type) {
        switch (type.getRawType()) {
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


    private class CompilingContext {
        public Module module;

        public String className;

        public String internalClassName;
    }
}