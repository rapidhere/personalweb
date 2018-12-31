import { rt } from '../lib/std';

export function random(a: i64): i64 {
    return rt.currentTimeMillis() + a;
}