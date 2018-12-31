/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.misc;

import com.ranttu.rapid.personalweb.core.wasm.misc.asm.tree.AbstractInsnNode;
import com.ranttu.rapid.personalweb.core.wasm.misc.asm.tree.MethodNode;
import lombok.experimental.UtilityClass;
import lombok.experimental.var;

/**
 * @author rapid
 * @version $Id: AsmHelper.java, v 0.1 2018-12-31 7:48 PM rapid Exp $
 */
@UtilityClass
public class AsmHelper {
    public void insertInsnAt(MethodNode methodNode, int location, AbstractInsnNode insn) {
        var insnList = methodNode.instructions;

        if (location < 0) {
            insnList.insert(insn);
        } else {
            insnList.insert(insnList.get(location), insn);
        }
    }
}