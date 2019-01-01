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
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.Handle;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.tree.MethodNode;
import com.ranttu.rapid.personalweb.core.wasm.model.FunctionElement;
import com.ranttu.rapid.personalweb.core.wasm.model.InstructionElement;
import com.ranttu.rapid.personalweb.core.wasm.model.Module;
import com.ranttu.rapid.personalweb.core.wasm.model.TypeElement;
import com.ranttu.rapid.personalweb.core.wasm.rt.WasmModule;
import com.ranttu.rapid.personalweb.core.wasm.rt.indy.IndyType;
import com.ranttu.rapid.personalweb.core.wasm.rt.indy.WasmBootstrapFactory;
import lombok.experimental.var;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ranttu.rapid.personalweb.core.wasm.misc.asm.Opcodes.*;
import static com.ranttu.rapid.personalweb.core.wasm.misc.asm.Type.getInternalName;
import static com.ranttu.rapid.personalweb.core.wasm.misc.asm.Type.getMethodDescriptor;

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
        cw.visit(V1_8,
            ACC_SYNTHETIC | ACC_SUPER | ACC_PUBLIC,
            ctx().internalClassName,
            null,
            getInternalName(WasmModule.class),
            new String[0]);
        cw.visitSource("<wasm>: " + ctx().sourceName, null);

        assembleMetas(cw);
        assembleConstructor(cw);
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

    private void assembleFunctions(ClassWriter cw) {
        var module = ctx().module;

        module.getFunctions().stream()
            .filter(functionElement -> !functionElement.isImported())
            .forEach(func -> {
                ctx().shouldEvalTypeClear();

                var mv = new MethodNode(
                    (func.isExported() ? ACC_PUBLIC : ACC_PRIVATE),
                    func.getName(),
                    func.getMethodDesc(),
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
            case BinCodes.OP_I32CONST: {
                mv.visitLdcInsn(instruction.getConst());
                ctx().evalPush(TypeElement.I32_TYPE);
                break;
            }
            case BinCodes.OP_I64CONST: {
                mv.visitLdcInsn(instruction.getConst());
                ctx().evalPush(TypeElement.I64_TYPE);
                break;
            }
            case BinCodes.OP_F32CONST: {
                mv.visitLdcInsn(instruction.getConst());
                ctx().evalPush(TypeElement.F32_TYPE);
                break;
            }
            case BinCodes.OP_F64CONST: {
                mv.visitLdcInsn(instruction.getConst());
                ctx().evalPush(TypeElement.F64_TYPE);
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
        if (func.isImported()) {
            if (func.isStaticImport()) {
                mv.visitVarInsn(ALOAD, 0);

                invokeDynamic(
                    mv,
                    IndyType.INVOKE_STATIC_IMPORT,
                    func.getName(),
                    func.getMethodDesc(),
                    func.getDelegateMethod()
                );
            } else if (func.isClassImport()) {
                invokeDynamic(
                    mv,
                    IndyType.INVOKE_CLASS_IMPORT,
                    func.getName(),
                    func.getMethodDesc(),
                    func.getDelegateMethod()
                );
            } else {
                throw new ShouldNotReach();
            }
        } else {
            mv.visitVarInsn(ALOAD, 0);

            invokeDynamic(
                mv,
                IndyType.INVOKE_LOCAL,
                func.getName(),
                func.getMethodDesc(),
                null
            );
        }

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
            throw new ShouldNotReach();
        }

        ctx().evalPop(1);
    }

    private void invokeDynamic(MethodNode mv, IndyType indyType, String methodName, String methodDesc, Method delegateMethod) {
        //noinspection ConstantConditions
        mv.visitInvokeDynamicInsn(
            methodName,
            methodDesc,
            new Handle(
                H_INVOKESTATIC,
                getInternalName(WasmBootstrapFactory.class),
                "bootstrap",
                WasmBootstrapFactory.MH_BOOTSTRAP.type().toMethodDescriptorString(),
                false
            ),

            // extra arguments
            indyType.name(),
            handleFromMethod(delegateMethod)
        );
    }

    private Handle handleFromMethod(Method method) {
        if (method == null) {
            return null;
        }

        return new Handle(
            Modifier.isStatic(method.getModifiers()) ? H_INVOKESTATIC : H_INVOKEVIRTUAL,
            getInternalName(method.getDeclaringClass()),
            method.getName(),
            getMethodDescriptor(method),
            false
        );
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