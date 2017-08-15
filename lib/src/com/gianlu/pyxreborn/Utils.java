package com.gianlu.pyxreborn;

import com.google.gson.JsonObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String printStackTrace(Throwable ex) {
        StringWriter string = new StringWriter();
        PrintWriter writer = new PrintWriter(string);
        ex.printStackTrace(writer);
        writer.close();
        return string.toString();
    }

    public static JsonObject event(Events event) {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.EVENT.toString(), event.toString());
        return obj;
    }

    public static List<Integer> toIntegersList(String array) {
        List<Integer> integers = new ArrayList<>();
        for (String split : array.split(",")) integers.add(Integer.parseInt(split));
        return integers;
    }
}
