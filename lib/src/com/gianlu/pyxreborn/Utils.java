package com.gianlu.pyxreborn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    public static List<Integer> toIntegersList(JsonArray array) {
        List<Integer> integers = new ArrayList<>();
        for (JsonElement element : array) integers.add(element.getAsInt());
        return integers;
    }
}
