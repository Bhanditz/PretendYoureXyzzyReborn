package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class Player implements Jsonable {
    private final User user;

    public Player(@NotNull User user) {
        this.user = user;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add(Fields.USER.toString(), user.toJson());
        return obj;
    }

    public User getUser() {
        return user;
    }
}
