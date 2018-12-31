/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.model;

import com.ranttu.rapid.personalweb.core.wasm.misc.$;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.var;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rapid
 * @version $Id: FunctionElement.java, v 0.1 2018-12-30 9:57 PM rapid Exp $
 */
@Getter
@Setter
public class FunctionElement extends ExposableElement {
    private boolean starter = false;

    private TypeElement resultType;

    private List<TypeElement> parameterTypes = new ArrayList<>();

    private List<TypeElement> localTypes = new ArrayList<>();

    private List<InstructionElement> instructions = new ArrayList<>();

    public String getDesc() {
        var sb = new StringBuilder();

        // parameters
        sb.append('(');
        parameterTypes
            .forEach(valueType -> sb.append(valueType.getJavaTypeDesc()));
        sb.append(')');

        // result
        sb.append(resultType.getJavaTypeDesc());

        return sb.toString();
    }

    public int getParameterSize() {
        return parameterTypes.size();
    }

    public boolean isStaticImport() {
        return isImported();
    }

    public String getStaticImportClassInternalName() {
        $.should(isStaticImport());

        return getImportModule().replace('.', '/');
    }

    public String getDeclarationName() {
        if (isStaticImport()) {
            return "STATIC_IMPORT$" + name;
        } else {
            return name;
        }
    }

    public int calculateLocalOffset(int localIdx) {
        int offset = 1;

        for (int i = 0; i < parameterTypes.size() && i < localIdx; i++) {
            offset += parameterTypes.get(i).getJavaTypeSize() / 4;
        }

        localIdx -= parameterTypes.size();
        for (int i = 0; i < localTypes.size() && i < localIdx; i++) {
            offset += localTypes.get(i).getJavaTypeSize() / 4;
        }
        return offset;
    }
}