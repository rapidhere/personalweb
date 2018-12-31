import { sys, Math } from '../lib/std';

export function random(a : i64) : i64 {
    return sys.currentTimeMillis() + a;
}

export function powMod(a: i64, b: i64, c: i64): i64 {
    return Math.powMod(a, b, c);
}