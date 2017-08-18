package com.gianlu.pyxreborn.Models.Client;

import com.gianlu.pyxreborn.Annotations.ClientSafe;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Jsonable;
import com.gianlu.pyxreborn.Models.Player;
import com.google.gson.JsonObject;

@ClientSafe
public class CPlayer implements Jsonable {
    public final CUser user;
    public final Player.Status status;
    public final int score;

    public CPlayer(JsonObject obj) {
        user = new CUser(obj.getAsJsonObject(Fields.USER.toString()));
        status = Player.Status.parse(obj.get(Fields.STATUS.toString()).getAsString());
        score = obj.get(Fields.SCORE.toString()).getAsInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CPlayer player = (CPlayer) o;
        return user.equals(player.user);
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add(Fields.USER.toString(), user.toJson());
        obj.addProperty(Fields.STATUS.toString(), status.toString());
        obj.addProperty(Fields.SCORE.toString(), score);
        return obj;
    }
}
