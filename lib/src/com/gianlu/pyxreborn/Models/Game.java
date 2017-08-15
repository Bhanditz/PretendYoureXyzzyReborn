package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game implements Jsonable {
    public final int gid;
    public final User host;
    public final List<Player> players;
    public final List<User> spectators;
    public Options options;
    public Status status = Status.LOBBY;

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

    @Nullable
    public Player findPlayerByNickname(String nickname) {
        for (Player player : players)
            if (Objects.equals(player.user.nickname, nickname))
                return player;

        return null;
    }

    public enum Status {
        LOBBY,
        PLAYING
    }

    public static class Options {
        private static final Options DEFAULT = new Options(10, 10, new ArrayList<>());
        public final int maxPlayers;
        public final int maxSpectators;
        public final List<Integer> cardSetIds;

        public Options(int maxPlayers, int maxSpectators, List<Integer> cardSetIds) {
            this.maxPlayers = maxPlayers;
            this.maxSpectators = maxSpectators;
            this.cardSetIds = cardSetIds;
        }
    }
}
