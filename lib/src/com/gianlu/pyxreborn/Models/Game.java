package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Game implements Jsonable {
    public final int gid;
    public final User host;
    public final List<User> players;

    public Game(int gid, User host) {
        this.gid = gid;
        this.host = host;
        this.players = new ArrayList<>();
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.GID.toString(), gid);
        obj.add(Fields.HOST.toString(), host.toJson());

        JsonArray players = new JsonArray();
        for (User player : this.players) players.add(player.toJson());
        obj.add(Fields.PLAYERS.toString(), players);

        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return gid == game.gid;
    }
}
