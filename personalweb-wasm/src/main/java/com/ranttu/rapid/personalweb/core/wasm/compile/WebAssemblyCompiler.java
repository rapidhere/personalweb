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
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.Type;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.tree.MethodNode;
import com.ranttu.rapid.personalweb.core.wasm.model.*;
import com.ranttu.rapid.personalweb.core.wasm.rt.Runtimes;
import com.ranttu.rapid.personalweb.core.wasm.rt.WasmModule;
import com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports;
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
    private static final short COMPILER_VERSION = 1;

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
     * compiling context holder
     */
    private final ThreadLocal<CompilingContext> CTX_HOLDER = new ThreadLocal<>();

    /**
     * get the compiled wasm module
     * NOTE: compiler will close the stream
     */
    public WasmModule compile(InputStream inputStream, String sourceName) {
        // parse source
        var module = wasmParser.parseModule(inputStream);

        // validate
        wasmValidator.validateModule(module);

        // compile
        return doCompile(module, sourceName);
    }

    private WasmModule doCompile(Module module, String sourceName) {
        var ctx = new CompilingContext();
        CTX_HOLDER.set(ctx);
        ctx.className = genClzName();
        ctx.module = module;
        ctx.sourceName = sourceName;

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
        cw.visitSource("<wasm>: " + ctx().sourceName, null);

        assembleMetas(cw);
        assembleConstructor(cw);
        assembleFunctionImports(cw);
        assembleFunctions(cw);

        cw.visitEnd();
        return cw.toByteArray();
    }

    private void assembleMetas(ClassWriter cw) {
        //~~~ getSourceName
        var mv = cw.visitMethod(
            ACC_PUBLIC,
            "getSourceName",
            "()Ljava/lang/String;",
            null,
            new String[0]
        );
        mv.visitCode();
        mv.visitLdcInsn(ctx().sourceName);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        //~~~ getMetaVersion
        mv = cw.visitMethod(
            ACC_PUBLIC,
            "getMetaVersion",
            "()Ljava/lang/String;",
            null,
            new String[0]
        );
        mv.visitCode();
        mv.visitLdcInsn(String.format("W%02xC%02x", COMPILER_VERSION, ctx().module.getVersion()));
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
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
                    ACC_PRIVATE + ACC_STATIC,
                    func.getDeclarationName(),
                    func.getDeclarationMethodDesc(ctx().internalClassName),
                    null,
                    new String[0]
                );
                // visit codes
                mv.visitCode();

                if (func.isStaticImport()) {
                    if (func.isBuiltin()) {
                        assembleBuiltins(mv, func);
                    } else {
                        assembleStaticImport(mv, func);
                    }
                } else if (func.isClassImport()) {
                    assembleClassImport(mv, func);
                }

                // TODO: support other imports
                mv.accept(cw);
            });
    }

    private void assembleClassImport(MethodNode mv, FunctionElement func) {
        // get java object on stack
        assembleLocalAccess(mv, func, 0, TypeElement.I32_TYPE, true, false);
        assembleObtainObject(
            mv,
            getInternalName(func.getDelegateMethod().getDeclaringClass()));

        // push parameters
        for (int idx = 1; idx < func.getParameterSize(); idx++) {
            var parType = func.getParameterTypes().get(idx);
            assembleLocalAccess(mv, func, idx, parType, true, false);
            checkAndCast(
                mv,
                ctx().currentType(),
                getInternalName(func.getDelegateMethod().getParameterTypes()[idx - 1])
            );
        }

        // call
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            func.getJavaClassInternalName(),
            func.getDelegateMethod().getName(),
            Type.getMethodDescriptor(func.getDelegateMethod()),
            func.getDelegateMethod().getDeclaringClass().isInterface()
        );

        // assemble return
        ctx().evalPop(func.getParameterSize());
        if (func.getResultType() != TypeElement.VOID_TYPE) {
            ctx().evalPush(func.getResultType());
        }
        assembleReturn(mv, func);
    }

    private void assembleStaticImport(MethodNode mv, FunctionElement func) {
        // push parameters
        for (int idx = 0; idx < func.getParameterSize(); idx++) {
            var parType = func.getParameterTypes().get(idx);
            assembleLocalAccess(mv, func, idx, parType, true, false);
            checkAndCast(
                mv,
                ctx().currentType(),
                getInternalName(func.getDelegateMethod().getParameterTypes()[idx])
            );
        }

        // call function
        mv.visitMethodInsn(
            INVOKESTATIC,
            func.getJavaClassInternalName(),
            func.getDelegateMethod().getName(),
            Type.getMethodDescriptor(func.getDelegateMethod()),
            false
        );

        // assemble return
        ctx().evalPop(func.getParameterSize());
        if (func.getResultType() != TypeElement.VOID_TYPE) {
            ctx().evalPush(func.getResultType());
        }
        assembleReturn(mv, func);
    }

    private void checkAndCast(MethodNode mv, TypeElement currentType, String targetClassType) {
        if (!currentType.getJavaTypeStr().equals(targetClassType)) {
            assembleObtainObject(mv, targetClassType);
        }
    }

    private void assembleObtainObject(MethodNode mv, String targetClassType) {
        mv.visitMethodInsn(
            INVOKESTATIC,
            getInternalName(Runtimes.class),
            "obtainObject",
            "(I)Ljava/lang/Object;",
            false
        );
        mv.visitTypeInsn(CHECKCAST, targetClassType);
    }

    private void assembleObjectIdentity(MethodNode mv) {
        mv.visitMethodInsn(
            INVOKESTATIC,
            getInternalName(Runtimes.class),
            "objectIdentity",
            "(Ljava/lang/Object;)I",
            false
        );
    }

    private void assembleBuiltins(MethodNode mv, FunctionElement func) {
        if (func.getImportModule().equals(SystemSupports.class.getName())) {
            // ref builtin
            if (func.getName().equals("ref")) {
                mv.visitVarInsn(ALOAD, func.calculateThisOffset());
                assembleObjectIdentity(mv);
                mv.visitInsn(IRETURN);
                return;
            }
        }

        throw new ShouldNotReach();
    }

    private void assembleFunctions(ClassWriter cw) {
        var module = ctx().module;

        module.getFunctions().stream()
            .filter(functionElement -> !functionElement.isImported())
            .forEach(func -> {
                ctx().shouldEvalTypeClear();

                var mv = new MethodNode(
                    (func.isExported() ? ACC_PUBLIC : ACC_PRIVATE) + ACC_STATIC,
                    func.getDeclarationName(),
                    func.getDeclarationMethodDesc(ctx().internalClassName),
                    null,
                    new String[0]
                );

                // visit codes
                mv.visitCode();
                func.getInstructions().forEach(instructionElement ->
                    assembleInstruction(mv, instructionElement));

                if (!ctx().isEvalTypeClear() || func.getResultType() == TypeElement.VOID_TYPE) {
                    assembleReturn(mv, func);
                }

                mv.accept(cw);
            });
    }

    private void assembleInstruction(MethodNode mv, InstructionElement instruction) {
        switch (instruction.getOpcode()) {
            case BinCodes.OP_LOCALGET: {
                assembleLocalAccess(mv,
                    instruction.getFunctionElement(),
                    instruction.getLocalIndex(),
                    instruction.getLocalType(),
                    true, false);
                break;
            }
            case BinCodes.OP_LOCALTEE: {
                assembleLocalAccess(mv,
                    instruction.getFunctionElement(),
                    instruction.getLocalIndex(),
                    instruction.getLocalType(),
                    true, true);
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
                assembleDirectCall(mv, instruction.getFunctionElement(), instruction.getCallFunction());
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

    private void assembleDirectCall(MethodNode mv, FunctionElement caller, FunctionElement func) {
        mv.visitVarInsn(ALOAD, caller.calculateThisOffset());
        mv.visitMethodInsn(
            INVOKESTATIC,
            ctx().internalClassName,
            func.getDeclarationName(),
            func.getDeclarationMethodDesc(ctx().internalClassName),
            false
        );

        ctx().evalPop(func.getParameterSize());
        if (func.getResultType() != TypeElement.VOID_TYPE) {
            ctx().evalPush(func.getResultType());
        }
    }

    private void assembleLocalAccess(MethodNode mv, FunctionElement func, int localIdx, TypeElement type, boolean load, boolean set) {
        var offset = func.calculateLocalOffset(localIdx);
        if (set) {
            ctx().evalPop(1);
        }
        if (load) {
            ctx().evalPush(type);
        }

        switch (type.getRawType()) {
            case BinCodes.VAL_I32:
                if (set) mv.visitVarInsn(ISTORE, offset);
                if (load) mv.visitVarInsn(ILOAD, offset);
                break;
            case BinCodes.VAL_F32:
                if (set) mv.visitVarInsn(FSTORE, offset);
                if (load) mv.visitVarInsn(FLOAD, offset);
                break;
            case BinCodes.VAL_I64:
                if (set) mv.visitVarInsn(LSTORE, offset);
                if (load) mv.visitVarInsn(LLOAD, offset);
                break;
            case BinCodes.VAL_F64:
                if (set) mv.visitVarInsn(DSTORE, offset);
                if (load) mv.visitVarInsn(DLOAD, offset);
                break;
            default:
                throw new ShouldNotReach();
        }
    }

    private void assembleReturn(MethodNode mv, FunctionElement func) {
        if (ctx().isEvalTypeClear()) {
            mv.visitInsn(RETURN);
            return;
        }

        var type = ctx().currentType();
        if (func.getDelegateMethod() == null
            || func.getDelegateMethod().getReturnType().getName().equals(type.getJavaTypeStr())) {
            if (type == TypeElement.I32_TYPE) {
                mv.visitInsn(IRETURN);
            } else if (type == TypeElement.I64_TYPE) {
                mv.visitInsn(LRETURN);
            } else if (type == TypeElement.F32_TYPE) {
                mv.visitInsn(FRETURN);
            } else if (type == TypeElement.F64_TYPE) {
                mv.visitInsn(DRETURN);
            } else {
                throw new ShouldNotReach();
            }
        } else {
            assembleObjectIdentity(mv);
            mv.visitInsn(IRETURN);
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

        public String sourceName;

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