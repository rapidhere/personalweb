import { rt, Math } from '../lib/std';

export function random(a: i64): i64 {
    return rt.currentTimeMillis() + a;
}

export function powMod(a: i64, n: i64, m: i64): i64 {
    return Math.powMod(a, n, m);
}