/// generated with j2w exported.
/// don't modify this file directly.

//////////// exported class: com.ranttu.rapid.personalweb.core.wasm.rt.WasmModule
export declare class com_ranttu_rapid_personalweb_core_wasm_rt_WasmModule {
  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.rt.WasmModule
   * @java_method getSourceName  
   * @java_invoke virtual
   */
  getSourceName(): string;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.rt.WasmModule
   * @java_method getMetaVersion  
   * @java_invoke virtual
   */
  getMetaVersion(): string;

}

export declare class string {
  
}

//////////// exported static: com.ranttu.rapid.personalweb.core.wasm.std.MathSupports
export declare namespace Math {
  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.MathSupports
   * @java_method sqrt double  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.MathSupports", "sqrt")
  export function sqrt($0: f64): f64;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.MathSupports
   * @java_method powMod long long long  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.MathSupports", "powMod")
  export function powMod($0: i64, $1: i64, $2: i64): i64;

}

//////////// exported static: com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
export declare namespace sys {
  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method exit int  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "exit")
  export function exit($0: i32): void;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method currentTimeMillis  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "currentTimeMillis")
  export function currentTimeMillis(): i64;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method err java.lang.String  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "err")
  export function err($0: string): void;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method ref  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "ref")
  export function ref(): com_ranttu_rapid_personalweb_core_wasm_rt_WasmModule;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method info java.lang.String  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "info")
  export function info($0: string): void;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method currentTimeNanos  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "currentTimeNanos")
  export function currentTimeNanos(): i64;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method infoln java.lang.String  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "infoln")
  export function infoln($0: string): void;

  /**
   * @java_class  com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
   * @java_method errln java.lang.String  
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "errln")
  export function errln($0: string): void;

}

