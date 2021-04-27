package org.dregs.garish.sql.data;


import java.util.HashMap;
import java.util.Map;

public class BuildMap<A, B> {
    private Map<A, B> map = new HashMap();

    public BuildMap() {
    }

    public Map<A, B> toMap() {
        return this.map;
    }

    public BuildMap<A, B> put(A key, B value) {
        this.map.put(key, value);
        return this;
    }

    public BuildMap<A, B> put(Tuple2<A, B> kv) {
        this.map.put(kv._1(), kv._2());
        return this;
    }

    public BuildMap<A, B> put(Map<A, B> map) {
        map.putAll(map);
        return this;
    }

    public static <K, V> Map<K, V> asHasMap(Tuple2<K, V>... kvs) {
        HashMap<K, V> hashMap = new HashMap();
        if (null != kvs && 0 != kvs.length) {
            Tuple2[] var2 = kvs;
            int var3 = kvs.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Tuple2<K, V> tuple2 = var2[var4];
                if (null != tuple2 && null != tuple2._1() && null != tuple2._2()) {
                    hashMap.put(tuple2._1(), tuple2._2());
                }
            }

            return hashMap;
        } else {
            return hashMap;
        }
    }

    public static <K, V> Map<K, V> asMap(Tuple2<K, V>... kvs) {
        Map<K, V> hashMap = new HashMap();
        if (null != kvs && 0 != kvs.length) {
            Tuple2[] var2 = kvs;
            int var3 = kvs.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Tuple2<K, V> tuple2 = var2[var4];
                if (null != tuple2) {
                    hashMap.put(tuple2._1(), tuple2._2());
                }
            }

            return hashMap;
        } else {
            return hashMap;
        }
    }

    public static <K> Map<K, Object> asMapSO(Tuple2<K, Object>... kvs) {
        return asMap(kvs);
    }

    public static <K, V> Tuple2<K, V> asNode(K key, V value) {
        return Tuple.initialize(key, value);
    }
}