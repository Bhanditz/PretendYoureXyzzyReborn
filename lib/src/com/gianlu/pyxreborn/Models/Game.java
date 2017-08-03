package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Game implements Jsonable {
    public final int gid;
    public final User host;
    public final List<Player> players;
    public final List<User> spectators;
    public Options options;

    public Game(int gid, User host) {
        this.gid = gid;
        this.host = host;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.options = Options.DEFAULT;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.GID.toString(), gid);
        obj.add(Fields.HOST.toString(), host.toJson());

        JsonArray players = new JsonArray();
        for (Player player : this.players) players.add(player.toJson());
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

    public static class Options {
        private static final Options DEFAULT = new Options(10, 10);
        public final int maxPlayers;
        public final int maxSpectators;

        public Options(int maxPlayers, int maxSpectators) {
            this.maxPlayers = maxPlayers;
            this.maxSpectators = maxSpectators;
        }
    }
}
