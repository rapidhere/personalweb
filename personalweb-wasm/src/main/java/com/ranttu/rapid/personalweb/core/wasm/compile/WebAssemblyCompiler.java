/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.ranttu.rapid.personalweb.core.wasm.compile;

import com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes;
import com.ranttu.rapid.personalweb.core.wasm.constants.ErrorCodes;
import com.ranttu.rapid.personalweb.core.wasm.exception.WasmCompilingException;
import com.ranttu.rapid.personalweb.core.wasm.exception.WasmException;
import com.ranttu.rapid.personalweb.core.wasm.exception.WasmUnknownError;
import com.ranttu.rapid.personalweb.core.wasm.model.*;
import lombok.experimental.var;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.ranttu.rapid.personalweb.core.wasm.constants.BinCodes.*;

/**
 * compiler for wasm
 *
 * @author rapid
 * @version $Id: WebAssemblyCompiler.java, v 0.1 2018Äê12ÔÂ07ÈÕ 5:00 PM rapid Exp $
 */
public class WebAssemblyCompiler {
    /**
     * get the compiled wasm module
     * NOTE: compiler will close the stream
     */
    public WasmModule compile(InputStream inputStream) {
        var moduleBuilder = WasmModule.builder();

        // transform module source
        parseModule(moduleBuilder, inputStream);

        return moduleBuilder.build();
    }

    /**
     * compile and generate a wasm module
     */
    private void parseModule(WasmModule.WasmModuleBuilder moduleBuilder, InputStream inputStream) {
        try {
            try {
                var ins = WasmSourceStream.of(inputStream);
                // read up magic number
                if (ins.next() != MOD_MAGICNUMBER0
                    || ins.next() != MOD_MAGICNUMBER1
                    || ins.next() != MOD_MAGICNUMBER2
                    || ins.next() != MOD_MAGICNUMBER3) {
                    throw new WasmCompilingException(ErrorCodes.MAGICNUMBER_CHECK_FAILED, "wrong magic number in input");
                }

                // check version
                if (ins.next() != MOD_VERSION0
                    || ins.next() != MOD_VERSION1
                    || ins.next() != MOD_VERSION2
                    || ins.next() != MOD_VERSION3) {
                    throw new WasmCompilingException(ErrorCodes.UNSUPPORTED_VERSION, "expect version 0x01");
                }
                // fixed version
                moduleBuilder.version((byte) 0x01);

                // parse sections
                while (ins.hasNext()) {
                    parseSection(moduleBuilder, ins);
                }
            } finally {
                inputStream.close();
            }
        } catch (WasmException e) {
            throw e;
        } catch (Throwable e) {
            throw new WasmUnknownError("unknown exception when compiling", e);
        }
    }

    /**
     * parse a section
     */
    private void parseSection(WasmModule.WasmModuleBuilder moduleBuilder, WasmSourceStream ins) {
        var sectionId = ins.next();
        switch (sectionId) {
            case SCT_CUSTOM:
                // custom section is omitted
                readSection(ins);
                break;
            case SCT_TYPE:
                moduleBuilder.typeSection(parseTypeSection(readSection(ins)));
                break;
            case SCT_IMPORT:
                moduleBuilder.importSection(parseImportSection(readSection(ins)));
                break;
            case SCT_FUNCTION:
                moduleBuilder.functionSection(parseFunctionSection(readSection(ins)));
                break;
            case SCT_TABLE:
                moduleBuilder.tableSection(parseTableSection(readSection(ins)));
                break;
            case SCT_MEMORY:
                moduleBuilder.memorySection(parseMemorySection(readSection(ins)));
                break;
            case SCT_GLOBAL:
                moduleBuilder.globalSection(parseGlobalSection(readSection(ins)));
                break;
            case SCT_EXPORT:
                moduleBuilder.exportSection(parseExportSection(readSection(ins)));
                break;
            case SCT_CODE:
                moduleBuilder.codeSection(parseCodeSection(readSection(ins)));
                break;
            case SCT_START:
            case SCT_ELEMENT:
            case SCT_DATA:
            default:
                throw new WasmCompilingException(ErrorCodes.UNKNOWN_SECTION, "unknown section: " + sectionId);
        }
    }

    /**
     * NOTE: suppose that section-id is read
     */
    private WasmSourceStream readSection(WasmSourceStream ins) {
        var len = ins.nextUInt();
        return ins.asSubStream(len);
    }

    private TypeSection parseTypeSection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var functionTypes = new FunctionType[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            if (secIns.next() != FUN_PREFIX) {
                throw new WasmCompilingException(ErrorCodes.UNEXPECTED_BYTE, "expect a function 0x60 prefix");
            }

            functionTypes[i] = parseFunctionType(secIns);
        }

        return new TypeSection(functionTypes);
    }

    private ImportSection parseImportSection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var imports = new ImportItem[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            var item = new ImportItem();
            imports[i] = item;

            item.setModule(parseName(secIns));
            item.setName(parseName(secIns));
            item.setImportType(secIns.next());

            switch (item.getImportType()) {
                case IMP_FUNCTION:
                    item.setFunctionIdx(secIns.nextUInt());
                    break;
                case IMP_TABLE:
                    item.setTableType(parseTableType(secIns));
                    break;
                case IMP_MEMORY:
                    item.setMemoryType(parseMemoryType(secIns));
                    break;
                case IMP_GLOBAL:
                    item.setGlobalType(parseGlobalType(secIns));
                    break;
                default:
                    throw new WasmCompilingException(
                        ErrorCodes.UNKNOWN_IMPORT_TYPE, "" + item.getImportType());
            }
        }

        return new ImportSection(imports);
    }

    private FunctionSection parseFunctionSection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var funcIndexes = new Long[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            funcIndexes[i] = secIns.nextUInt();
        }

        return new FunctionSection(funcIndexes);
    }

    private TableSection parseTableSection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var tblTypes = new TableType[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            tblTypes[i] = parseTableType(secIns);
        }

        return new TableSection(tblTypes);
    }

    private MemorySection parseMemorySection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var memTypes = new MemoryType[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            memTypes[i] = parseMemoryType(secIns);
        }

        return new MemorySection(memTypes);
    }

    private GlobalSection parseGlobalSection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var glbItems = new GlobalItem[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            glbItems[i] = new GlobalItem(parseGlobalType(secIns), readInstructions(secIns));
        }

        return new GlobalSection(glbItems);
    }

    private WasmInstruction[] readInstructions(WasmSourceStream ins) {
        var exprs = new ArrayList<WasmInstruction>();

        while (ins.peek() != EXP_TERMINATE) {
            exprs.add(parseInstruction(ins));
        }
        // read up EXP_TERMINATE
        ins.next();

        return exprs.toArray(new WasmInstruction[0]);
    }

    private ExportSection parseExportSection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var expItems = new ExportItem[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            var itemBuilder = ExportItem.builder();
            itemBuilder.name(parseName(secIns));
            itemBuilder.exportType(secIns.next());
            itemBuilder.exportIndex(secIns.nextUInt());

            expItems[i] = itemBuilder.build();

            // check code
            var exportType = expItems[i].getExportType();
            if (exportType != EXP_FUNCTION && exportType != EXP_TABLE && exportType != EXP_MEMORY && exportType != EXP_GLOBAL) {
                throw new WasmCompilingException(ErrorCodes.UNKNOWN_EXPORT_TYPE, "" + exportType);
            }
        }

        return new ExportSection(expItems);
    }

    private CodeSection parseCodeSection(WasmSourceStream secIns) {
        var cnt = secIns.nextUInt();
        var codeItems = new CodeItem[(int) cnt];

        for (int i = 0; i < cnt; i++) {
            var itemBuilder = CodeItem.builder();
            var length = secIns.nextUInt();
            parseFunction(itemBuilder, secIns.asSubStream(length));
            codeItems[i] = itemBuilder.build();
        }

        return new CodeSection(codeItems);
    }

    private void parseFunction(CodeItem.CodeItemBuilder codeBuilder, WasmSourceStream funIns) {
        // read locals
        var localCnt = funIns.nextUInt();
        var locals = new ValueType[(int) localCnt];
        var localCounts = new long[(int) localCnt];

        for (int i = 0; i < localCnt; i++) {
            localCounts[i] = funIns.nextUInt();
            locals[i] = parseValueType(funIns);
        }
        codeBuilder.locals(locals);
        codeBuilder.localCounts(localCounts);

        // read instructions
        codeBuilder.instructions(readInstructions(funIns));
    }

    private WasmInstruction parseInstruction(WasmSourceStream ins) {
        var opcode = ins.next();
        var instBuilder = WasmInstruction.builder().opcode(opcode);

        switch (opcode) {
            //~~~ no arg instruction
            case OP_UNREACHABLE:
            case OP_NOP:
            case OP_RETURN:
            case OP_I32ADD: {
                return instBuilder.build();
            }
            //~~~ blocked instruction
            case OP_BLOCK:
            case OP_LOOP: {
                instBuilder.blockType(parseBlockType(ins));
                var insts = new ArrayList<WasmInstruction>();
                while (ins.peek() != BLK_END) {
                    insts.add(parseInstruction(ins));
                }
                // read up BLK_END
                ins.next();

                return instBuilder
                    .blockInstructions(insts.toArray(new WasmInstruction[0])).build();
            }
            //~~~ if instruction
            case OP_IF: {
                instBuilder.blockType(parseBlockType(ins));

                var insts = new ArrayList<WasmInstruction>();
                while (ins.peek() != BLK_END && ins.peek() != OP_ELSE) {
                    insts.add(parseInstruction(ins));
                }
                instBuilder.blockInstructions(insts.toArray(new WasmInstruction[0]));
                insts.clear();

                // have else statements
                if (ins.next() == OP_ELSE) {
                    while (ins.peek() != BLK_END) {
                        insts.add(parseInstruction(ins));
                    }
                    // read up BLK_END
                    ins.next();
                }

                return instBuilder
                    .elseInstructions(insts.toArray(new WasmInstruction[0])).build();
            }
            //~~~ labeled instruction
            case OP_BR:
            case OP_BRIF: {
                return instBuilder.labelIndex(ins.nextUInt()).build();
            }
            //~~~ call instruction
            case OP_CALL: {
                return instBuilder.functionIndex(ins.nextUInt()).build();
            }
            //~~~ call indirect instruction
            //~~~ local var instruction
            case OP_LOCALGET:
            case OP_LOCALSET:
            case OP_LOCALTEE: {
                return instBuilder.localIndex(ins.nextUInt()).build();
            }
            //~~~ global var instruction
            case OP_GLOBALGET:
            case OP_GLOBALSET: {
                return instBuilder.globalIndex(ins.nextUInt()).build();
            }
            //~~~ memory instruction
            //~~~ special memory instruction
            //~~~ ldc instruction
            case OP_I32CONST:
            case OP_I64CONST: {
                return instBuilder.intConst(ins.nextSNumber()).build();
            }
            case OP_F32CONST: {
                return instBuilder.floatConst(ins.nextF32()).build();
            }
            case OP_F64CONST: {
                return instBuilder.floatConst(ins.nextF64()).build();
            }

            default:
                throw new WasmCompilingException(ErrorCodes.UNKNOWN_OPCODE, "unknown opcode: " + opcode);
        }
    }

    private BlockType parseBlockType(WasmSourceStream ins) {
        if (ins.next() != BLK_PREFIX) {
            throw new WasmCompilingException(ErrorCodes.UNEXPECTED_BYTE, "block type should has byte code 0x40");
        }

        return new BlockType(parseValueType(ins));
    }

    private TableType parseTableType(WasmSourceStream ins) {
        if (ins.next() != TBL_ELEMENT) {
            throw new WasmCompilingException(ErrorCodes.UNEXPECTED_BYTE, "element type should has byte code 0x70");
        }

        return new TableType(parseLimitInfo(ins));
    }

    private MemoryType parseMemoryType(WasmSourceStream ins) {
        return new MemoryType(parseLimitInfo(ins));
    }

    private GlobalType parseGlobalType(WasmSourceStream ins) {
        var valType = parseValueType(ins);
        var mutFlag = ins.next();

        if (mutFlag != BinCodes.GLB_CONST && mutFlag != BinCodes.GLB_VAR) {
            throw new WasmCompilingException(ErrorCodes.UNEXPECTED_BYTE, "unknown global import type " + mutFlag);
        }

        return new GlobalType(valType, mutFlag);
    }

    private LimitInfo parseLimitInfo(WasmSourceStream ins) {
        var type = ins.next();
        var minimum = ins.nextUInt();
        var maximum = Long.MAX_VALUE;

        if (type == LIM_WITHMAX) {
            maximum = ins.nextUInt();
        } else if (type != LIM_NOMAX) {
            throw new WasmCompilingException(ErrorCodes.UNKNOWN_LIMIT_TYPE, "" + type);
        }

        return new LimitInfo(type, minimum, maximum);
    }

    /**
     * NOTE: suppose that 0x60 is read
     */
    private FunctionType parseFunctionType(WasmSourceStream ins) {
        var functionType = new FunctionType();
        // read input
        var nInput = ins.nextUInt();
        functionType.setParameters(new ValueType[(int) nInput]);
        for (int i = 0; i < nInput; i++) {
            functionType.getParameters()[i] = parseValueType(ins);
        }

        // read output
        var nOutput = ins.nextUInt();
        if (nOutput > 1) {
            throw new WasmCompilingException(ErrorCodes.UNSUPPORTED_FEATURE,
                "multiple return function type is not supported");
        }

        if (nOutput > 0) {
            functionType.setResult(parseValueType(ins));
        } else {
            functionType.setResult(null);
        }

        return functionType;
    }

    private ValueType parseValueType(WasmSourceStream ins) {
        var code = ins.next();
        switch (code) {
            case VAL_F32:
            case VAL_F64:
            case VAL_I32:
            case VAL_I64:
                return new ValueType(code);
            default:
                throw new WasmCompilingException(ErrorCodes.UNKNOWN_VALUE_TYPE, "" + code);
        }
    }

    private String parseName(WasmSourceStream ins) {
        var length = ins.nextUInt();
        var buff = new byte[(int) length];
        ins.readBytes(buff);

        return new String(buff, Charset.forName("UTF-8"));
    }
}