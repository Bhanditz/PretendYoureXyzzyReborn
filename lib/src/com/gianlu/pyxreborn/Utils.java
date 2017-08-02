package com.gianlu.pyxreborn;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
