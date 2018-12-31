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
import com.ranttu.rapid.personalweb.core.wasm.misc.AsmHelper;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.ClassWriter;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.tree.MethodNode;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.tree.VarInsnNode;
import com.ranttu.rapid.personalweb.core.wasm.model.*;
import com.ranttu.rapid.personalweb.core.wasm.model.runtime.WasmModule;
import lombok.experimental.var;

import java.io.InputStream;
import java.util.Stack;
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

    private final ThreadLocal<CompilingContext> CTX_HOLDER = new ThreadLocal<>();

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
        CTX_HOLDER.set(ctx);
        ctx.className = genClzName();
        ctx.module = module;

        var bytes = assembleModuleClass();
        $.printClass(ctx.className, bytes);

        try {
            // define class and return module instance
            Class<?> c = wasmModuleClassLoader.defineClass(ctx.className, bytes);
            return (WasmModule) c.newInstance();
        } catch (Throwable e) {
            throw new WasmCompilingException(
                ErrorCodes.UNKNOWN_ERROR, "failed to create module class", e);
        } finally {
            CTX_HOLDER.remove();
        }
    }

    private CompilingContext ctx() {
        return CTX_HOLDER.get();
    }

    private byte[] assembleModuleClass() {
        // set internal class name
        ctx().internalClassName = ctx().className.replace('.', '/');

        var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_6,
            ACC_SYNTHETIC | ACC_SUPER | ACC_PUBLIC,
            ctx().internalClassName,
            null,
            getInternalName(WasmModule.class),
            new String[0]);
        cw.visitSource("<wasm>", null);

        assembleConstructor(cw);
        assembleFunctionImports(cw);
        assembleFunctions(cw);

        cw.visitEnd();
        return cw.toByteArray();
    }

    private void assembleConstructor(ClassWriter cw) {
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

    private void assembleFunctionImports(ClassWriter cw) {
        ctx().module.getFunctions().stream()
            .filter(ExposableElement::isImported)
            .forEach(func -> {
                ctx().shouldEvalTypeClear();

                var mv = new MethodNode(
                    ACC_PRIVATE,
                    func.getDeclarationName(),
                    func.getDesc(),
                    null,
                    new String[0]
                );

                // visit codes
                mv.visitCode();

                if (func.isStaticImport()) {
                    // push parameters
                    for (int idx = 0; idx < func.getParameterSize(); idx++) {
                        var parType = func.getParameterTypes().get(idx);
                        assembleLocalGet(mv, func, idx, parType);
                    }

                    // call function
                    mv.visitMethodInsn(
                        INVOKESTATIC,
                        func.getStaticImportClassInternalName(),
                        func.getName(),
                        func.getDesc(),
                        false
                    );

                    // assemble return
                    ctx().evalPop(func.getParameterSize());
                    ctx().evalPush(func.getResultType());
                    assembleReturn(mv);
                }
                // TODO: support other imports

                mv.accept(cw);
            });
    }

    private void assembleFunctions(ClassWriter cw) {
        var module = ctx().module;

        module.getFunctions().stream()
            .filter(functionElement -> !functionElement.isImported())
            .forEach(func -> {
                ctx().shouldEvalTypeClear();

                var mv = new MethodNode(
                    func.isExported() ? ACC_PUBLIC : ACC_PRIVATE,
                    func.getDeclarationName(),
                    func.getDesc(),
                    null,
                    new String[0]
                );

                // visit codes
                mv.visitCode();
                func.getInstructions().forEach(instructionElement ->
                    assembleInstruction(mv, instructionElement));

                if (!ctx().isEvalTypeClear() || func.getResultType() == TypeElement.VOID_TYPE) {
                    assembleReturn(mv);
                }

                mv.accept(cw);
            });
    }

    private void assembleInstruction(MethodNode mv, InstructionElement instruction) {
        switch (instruction.getOpcode()) {
            case BinCodes.OP_LOCALGET: {
                assembleLocalGet(mv,
                    instruction.getFunctionElement(),
                    instruction.getLocalIndex(),
                    instruction.getLocalType());
                break;
            }
            case BinCodes.OP_I32ADD: {
                mv.visitInsn(IADD);
                ctx().evalPop(2);
                ctx().evalPush(TypeElement.I32_TYPE);
                break;
            }
            case BinCodes.OP_I64ADD: {
                mv.visitInsn(LADD);
                ctx().evalPop(2);
                ctx().evalPush(TypeElement.I64_TYPE);
                break;
            }
            case BinCodes.OP_CALL: {
                assembleDirectCall(mv, instruction.getCallFunction());
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

    private void assembleDirectCall(MethodNode mv, FunctionElement func) {
        AsmHelper.insertInsnAt(
            mv,
            mv.instructions.size() - func.getParameterSize() - 1,
            new VarInsnNode(ALOAD, 0));

        mv.visitMethodInsn(
            INVOKESPECIAL,
            ctx().internalClassName,
            func.getDeclarationName(),
            func.getDesc(),
            false
        );

        ctx().evalPop(func.getParameterSize());
        ctx().evalPush(func.getResultType());
    }

    private void assembleLocalGet(MethodNode mv, FunctionElement func, int localIdx, TypeElement type) {
        ctx().evalPush(type);
        var offset = func.calculateLocalOffset(localIdx);

        switch (type.getRawType()) {
            case BinCodes.VAL_I32:
                mv.visitVarInsn(ILOAD, offset);
                break;
            case BinCodes.VAL_F32:
                mv.visitVarInsn(FLOAD, offset);
                break;
            case BinCodes.VAL_I64:
                mv.visitVarInsn(LLOAD, offset);
                break;
            case BinCodes.VAL_F64:
                mv.visitVarInsn(DLOAD, offset);
                break;
            default:
                throw new ShouldNotReach();
        }
    }

    private void assembleReturn(MethodNode mv) {
        if (ctx().isEvalTypeClear()) {
            mv.visitInsn(RETURN);
            return;
        }

        var type = ctx().currentType();
        if (type == TypeElement.I32_TYPE) {
            mv.visitInsn(IRETURN);
        } else if (type == TypeElement.I64_TYPE) {
            mv.visitInsn(LRETURN);
        } else if (type == TypeElement.F32_TYPE) {
            mv.visitInsn(FRETURN);
        } else if (type == TypeElement.F64_TYPE) {
            mv.visitInsn(DRETURN);
        } else {
            mv.visitInsn(ARETURN);
        }

        ctx().evalPop(1);
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

        public Stack<TypeElement> evalTypeStack = new Stack<>();

        public void evalPop(int cnt) {
            for (int i = 0; i < cnt; i++) {
                evalTypeStack.pop();
            }
        }

        public TypeElement currentType() {
            return evalTypeStack.peek();
        }

        public TypeElement evalPush(TypeElement typeElement) {
            return evalTypeStack.push(typeElement);
        }

        public void shouldEvalTypeClear() {
            $.should(isEvalTypeClear());
        }

        public boolean isEvalTypeClear() {
            return evalTypeStack.isEmpty();
        }
    }
}