package com.gianlu.pyxreborn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static <K, V> Map<K, V> singletonMap(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static String printStackTrace(Throwable ex) {
        StringWriter string = new StringWriter();
        PrintWriter writer = new PrintWriter(string);
        ex.printStackTrace(writer);
        writer.close();
        return string.toString();
    }
}
