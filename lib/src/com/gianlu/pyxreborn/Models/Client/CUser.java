package com.gianlu.pyxreborn.Models.Client;

import com.gianlu.pyxreborn.Annotations.ClientSafe;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Jsonable;
import com.google.gson.JsonObject;

@ClientSafe
public class CUser implements Jsonable {
    public final String nickname;

    public CUser(JsonObject obj) {
        nickname = obj.get(Fields.NICKNAME.toString()).getAsString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CUser user = (CUser) o;
        return nickname.equals(user.nickname);
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.NICKNAME.toString(), nickname);
        return obj;
    }
}
