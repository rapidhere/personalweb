import { java_util_Map, allocateMap } from '../lib/std';

export function random(): java_util_Map<i64, i64> {
    let map : java_util_Map<i64, i64> = allocateMap();
    map.put(1, 2);
    return map;
}