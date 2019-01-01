import { 
    sys, 
    com_ranttu_rapid_personalweb_core_wasm_rt_WasmModule as WasmModule 
} from '../lib/std';

export function testMeta(): void {
    let module: WasmModule = sys.ref();
    sys.infoln(module.getSourceName());
    sys.infoln(module.getMetaVersion());

    sys.printModuleMeta(true, true);
    sys.printModuleMeta(true, false);
    sys.printModuleMeta(false, false);
    sys.printModuleMeta(false, true);
}