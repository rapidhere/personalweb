/// generated with j2w exported.
/// don't modify this file directly.

//////////// exported static: com.ranttu.rapid.personalweb.core.wasm.std.MathSupports
export declare namespace Math {
  @java_class("com.ranttu.rapid.personalweb.core.wasm.std.MathSupports")
  @java_method("sqrt", "double")
  export function sqrt($0: f64): f64;

  @java_class("com.ranttu.rapid.personalweb.core.wasm.std.MathSupports")
  @java_method("powMod", "long", "long", "long")
  export function powMod($0: i64, $1: i64, $2: i64): i64;

}

//////////// exported static: com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports
export declare namespace rt {
  @java_class("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports")
  @java_method("exit", "int")
  export function exit($0: i32): void;

  @java_class("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports")
  @java_method("currentTimeMillis")
  export function currentTimeMillis(): i64;

  @java_class("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports")
  @java_method("currentTimeNanos")
  export function currentTimeNanos(): i64;

}

