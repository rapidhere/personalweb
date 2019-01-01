/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.rt.indy;

import com.ranttu.rapid.personalweb.core.wasm.exception.ShouldNotReach;
import com.ranttu.rapid.personalweb.core.wasm.misc.$;
import com.ranttu.rapid.personalweb.core.wasm.misc.MethodHandleHelper;
import com.ranttu.rapid.personalweb.core.wasm.rt.Runtimes;
import com.ranttu.rapid.personalweb.core.wasm.rt.WasmModule;
import lombok.SneakyThrows;

import java.lang.invoke.*;

import static java.lang.invoke.MethodType.methodType;

/**
 * @author rapid
 * @version $Id: WasmBootstrapHolder.java, v 0.1 2019-01-01 8:39 PM rapid Exp $
 */
final public class WasmBootstrapFactory {
    private WasmBootstrapFactory() {
    }

    // for compile usage only
    public static final MethodHandle MH_BOOTSTRAP = MethodHandleHelper.mh(WasmBootstrapFactory.class, "bootstrap");

    @SneakyThrows
    public static CallSite bootstrap(
        MethodHandles.Lookup lookup,
        String methodName,
        MethodType methodType,
        String indyTypeStr,
        MethodHandle targetMh
    ) {
        IndyType indyType = IndyType.valueOf(indyTypeStr);

        switch (indyType) {
            case INVOKE_LOCAL:
                return composeLocalCallSite(lookup, methodName, methodType);
            case INVOKE_STATIC_IMPORT:
                return composeStaticImportCallSite(methodType, targetMh);
            case INVOKE_CLASS_IMPORT:
                return composeClassImportCallSite(methodType, targetMh);
            default:
                throw new ShouldNotReach();
        }
    }

    @SneakyThrows
    private static CallSite composeLocalCallSite(MethodHandles.Lookup lookup, String methodName,
                                                 MethodType methodType) {
        MethodHandle mh = lookup.findVirtual(lookup.lookupClass(), methodName, methodType);
        mh = arrangeModuleParameter(mh, methodType, true, lookup.lookupClass());

        return new ConstantCallSite(mh);
    }

    @SneakyThrows
    private static CallSite composeStaticImportCallSite(MethodType inputMt, MethodHandle targetMh) {
        MethodType targetMt = targetMh.type();
        boolean prependModule = targetMt.parameterCount() > 0 && targetMt.parameterType(0).isAssignableFrom(WasmModule.class);

        MethodHandle wrapMh = targetMh;
        // 4. convert primitive types
        wrapMh = convertArgumentTypes(
            wrapMh,
            prependModule ?
                inputMt
                    .dropParameterTypes(inputMt.parameterCount() - 1, inputMt.parameterCount())
                    .insertParameterTypes(0, WasmModule.class)
                :
                inputMt.dropParameterTypes(inputMt.parameterCount() - 1, inputMt.parameterCount())
        );
        // 3. result reference -> identity
        wrapMh = filterResult(wrapMh);
        // 2. object identity -> object reference
        wrapMh = filterArguments(wrapMh, prependModule ? 1 : 0);
        // 1. a,b,c,module -> a,b,c or a,b,c,module -> module,a,b,c
        wrapMh = arrangeModuleParameter(wrapMh, inputMt, prependModule, WasmModule.class);

        return new ConstantCallSite(wrapMh);
    }

    @SneakyThrows
    private static CallSite composeClassImportCallSite(MethodType inputMt, MethodHandle targetMh) {
        MethodHandle wrapMh = targetMh;

        // 3. convert primitive types;
        convertArgumentTypes(wrapMh, inputMt);
        // 2. filter result
        wrapMh = filterResult(wrapMh);
        // 1. object identity -> object reference
        wrapMh = filterArguments(wrapMh, 0);

        return new ConstantCallSite(wrapMh);
    }

    private static MethodHandle convertArgumentTypes(MethodHandle targetMh, MethodType inputMt) {
        MethodType newMt = inputMt;

        for (int i = 0; i < inputMt.parameterCount(); i++) {
            if (!targetMh.type().parameterType(i).isPrimitive()
                && inputMt.parameterType(i).isPrimitive()) {
                newMt = newMt.changeParameterType(i, targetMh.type().parameterType(i));
            }
        }

        if (!targetMh.type().returnType().isPrimitive()) {
            newMt = newMt.changeReturnType(targetMh.type().returnType());
        }

        return MethodHandles.explicitCastArguments(targetMh, newMt);
    }

    private static MethodHandle filterArguments(MethodHandle mh, int skipped) {
        MethodType targetType = mh.type();

        MethodHandle[] argFilters = new MethodHandle[targetType.parameterCount()];
        for (int i = 0; i < skipped; i++) {
            argFilters[i] = MethodHandles.identity(targetType.parameterType(i));
        }

        for (int i = skipped; i < targetType.parameterCount(); i++) {
            Class<?> targetParType = targetType.parameterType(i);

            // for non primitives, de-reference
            if (!targetParType.isPrimitive()) {
                argFilters[i] = Runtimes.MH_OBTAIN_OBJECT
                    .asType(methodType(targetParType, int.class));
            } else {
                argFilters[i] = MethodHandles.identity(targetParType);
            }
        }
        return MethodHandles.filterArguments(mh, 0, argFilters);
    }

    private static MethodHandle arrangeModuleParameter(MethodHandle targetMh, MethodType inputMt, boolean prependModule, Class<?> moduleClass) {
        int parameterCount = inputMt.parameterCount();

        // reorder module parameter to the first place
        if (prependModule) {
            int[] reordered = new int[parameterCount];
            for (int i = 1; i < parameterCount; i++) {
                reordered[i] = i - 1;
            }
            reordered[0] = parameterCount - 1;

            return MethodHandles.permuteArguments(targetMh, inputMt, reordered);
        }
        // drop last module parameter
        else {
            return MethodHandles.dropArguments(targetMh, parameterCount - 1, moduleClass);
        }
    }

    private static MethodHandle filterResult(MethodHandle mh) {
        if (mh.type().returnType() == void.class) {
            return mh;
        }

        //~~~ return filter
        MethodHandle returnFilter;
        // for non-primitive returns, de-reference
        if (!mh.type().returnType().isPrimitive()) {
            returnFilter = Runtimes.MH_OBJECT_IDENTITY.asType(methodType(int.class, mh.type().returnType()));
        } else {
            returnFilter = MethodHandles.identity(mh.type().returnType());
        }
        $.should(returnFilter != null);
        return MethodHandles.filterReturnValue(mh, returnFilter);
    }
}