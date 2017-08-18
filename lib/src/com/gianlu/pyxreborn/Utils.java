package com.gianlu.pyxreborn;

import com.gianlu.pyxreborn.Models.Client.CPlayer;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public static List<JsonObject> toJsonObjectsList(JsonArray array) {
        List<JsonObject> objs = new ArrayList<>();
        for (JsonElement element : array) objs.add(element.getAsJsonObject());
        return objs;
    }

    @Nullable
    public static CPlayer find(List<CPlayer> players, String nickname) {
        for (CPlayer player : players)
            if (Objects.equals(player.user.nickname, nickname))
                return player;

        return null;
    }

    public static JsonArray toJsonArray(List<?> items) {
        JsonArray array = new JsonArray();
        for (Object obj : items) array.add(obj.toString());
        return array;
    }

    public static List<Integer> toIntegersList(JsonArray array) {
        List<Integer> ints = new ArrayList<>();
        for (JsonElement element : array) ints.add(element.getAsInt());
        return ints;
    }

    public static List<Integer> toIntegersList(String array) {
        List<Integer> integers = new ArrayList<>();
        for (String split : array.split(",")) integers.add(Integer.parseInt(split));
        return integers;
    }

    public static <T> List<T> toList(JsonArray array, Class<T> targetClass) {
        List<T> items = new ArrayList<>();
        for (JsonElement element : array) {
            try {
                items.add(targetClass.getConstructor(JsonObject.class).newInstance(element.getAsJsonObject()));
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        return items;
    }
}
