package com.gianlu.pyxreborn;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String printStackTrace(Throwable ex) {
        StringWriter string = new StringWriter();
        PrintWriter writer = new PrintWriter(string);
        ex.printStackTrace(writer);
        writer.close();
        return string.toString();
    }

    @NotNull
    public static String generateAlphanumericString(int length) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i <= length - 1; i++) {
            if (random.nextBoolean()) builder.append(String.valueOf(random.nextInt(10)));
            else builder.append(Utils.ALPHABET.charAt(random.nextInt(Utils.ALPHABET.length())));
        }

        return builder.toString();
    }

    public static JsonObject event(Events event) {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.EVENT.toString(), event.toString());
        return obj;
    }

    public static JsonArray toJsonArray(List<?> items) {
        JsonArray array = new JsonArray();
        for (Object obj : items) array.add(obj.toString());
        return array;
    }

    public static List<Integer> toIntegersList(String array) {
        List<Integer> integers = new ArrayList<>();
        for (String split : array.split(",")) integers.add(Integer.parseInt(split));
        return integers;
    }
}
