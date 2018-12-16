/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.constants;

/**
 * binary codes constants for wasm
 *
 * @author rapid
 * @version $Id: BinCodes.java, v 0.1 2018-12-08 2:30 PM rapid Exp $
 */
public interface BinCodes {
    //~~~ magic number
    byte MOD_MAGICNUMBER0 = 0x00;   // '\0'
    byte MOD_MAGICNUMBER1 = 0x61;   // 'a'
    byte MOD_MAGICNUMBER2 = 0x73;   // 's'
    byte MOD_MAGICNUMBER3 = 0x6d;   // 'm'

    //~~~ version
    byte MOD_VERSION0 = 0x01;
    byte MOD_VERSION1 = 0x00;
    byte MOD_VERSION2 = 0x00;
    byte MOD_VERSION3 = 0x00;

    //~~~ value types
    byte VAL_I32 = 0x7F;
    byte VAL_I64 = 0x7E;
    byte VAL_F32 = 0x7D;
    byte VAL_F64 = 0x7C;

    //~~~ result types
    byte RES_EMPTY = 0x40;           // empty result

    //~~~ function types
    byte FUN_PREFIX = 0x60;          // function type prefix

    //~~~ limit types
    byte LIM_NOMAX = 0x00;           // a limit type without maximum
    byte LIM_WITHMAX = 0x01;         // a limit type with maximum

    //~~~ memory types
    // simply leave as blank

    //~~~ table types
    byte TBL_ELEMENT = 0x70;         // indicate the start of table element(s)

    //~~~ element types
    // simply leave as blank

    //~~~ global types
    byte GLB_CONST = 0x00;
    byte GLB_VAR = 0x01;

    //~~~ instructions
    byte OP_UNREACHABLE = 0x00;
    byte OP_NOP = 0x01;
    byte OP_BLOCK = 0x02;
    byte OP_LOOP = 0x03;
    byte OP_IF = 0x04;
    byte OP_ELSE = 0x05;
    byte OP_BR = 0x0C;
    byte OP_BRIF = 0x0D;
    byte OP_BRTABLE = 0x0E;
    byte OP_RETURN = 0x0F;
    byte OP_CALL = 0x10;
    byte OP_CALLINDIRECT = 0x11;
    byte OP_LOCALGET = 0x20;
    byte OP_LOCALSET = 0x21;
    byte OP_LOCALTEE = 0x22;
    byte OP_GLOBALGET = 0x23;
    byte OP_GLOBALSET = 0x24;
    byte OP_I32CONST = 0x41;
    byte OP_I64CONST = 0x42;
    byte OP_F32CONST = 0x43;
    byte OP_F64CONST = 0x44;
    byte OP_DROP = 0x1A;
    byte OP_SELECT = 0x1B;
    byte OP_I32ADD = 0x6A;
    byte OP_I64ADD = 0x7C;


    byte BLK_END = 0x0B;

    //~~~ sections
    byte SCT_CUSTOM = 0x00;
    byte SCT_TYPE = 0x01;
    byte SCT_IMPORT = 0x02;
    byte SCT_FUNCTION = 0x03;
    byte SCT_TABLE = 0x04;
    byte SCT_MEMORY = 0x05;
    byte SCT_GLOBAL = 0x06;
    byte SCT_EXPORT = 0x07;
    byte SCT_START = 0x08;
    byte SCT_ELEMENT = 0x09;
    byte SCT_CODE = 0x0A;
    byte SCT_DATA = 0x0B;

    //~~~ import types
    byte IMP_FUNCTION = 0x00;
    byte IMP_TABLE = 0x01;
    byte IMP_MEMORY = 0x02;
    byte IMP_GLOBAL = 0x03;

    //~~~ export types
    byte EXP_FUNCTION = 0x00;
    byte EXP_TABLE = 0x01;
    byte EXP_MEMORY = 0x02;
    byte EXP_GLOBAL = 0x03;

    //~~~ expressions
    byte EXP_TERMINATE = 0x0B;

    //~~~ block types
    byte BLK_PREFIX = 0x40;
}