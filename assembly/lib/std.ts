/// generated with j2w exported.
/// don't modify this file directly.
export declare class java_util_Map<K, V> {
  get($0: K): V;
  put($0: K, $1 : V) : void;
}

export declare function allocateMap(): java_util_Map<i64, i64>;


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
export declare namespace rt {
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
   * @java_method currentTimeNanos
   * @java_invoke static
   */
  @external("com.ranttu.rapid.personalweb.core.wasm.std.SystemSupports", "currentTimeNanos")
  export function currentTimeNanos(): i64;

}

