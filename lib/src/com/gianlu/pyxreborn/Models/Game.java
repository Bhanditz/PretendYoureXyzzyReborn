package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game implements Jsonable {
    public final int gid;
    public final ObservableList<Player> players;
    public final ObservableList<User> spectators;
    public User host;
    public Options options;
    public Status status = Status.LOBBY;

    public Game(int gid, User host) {
        this.gid = gid;
        this.host = host;
        this.players = new ObservableListWrapper<>(new ArrayList<>());
        this.spectators = new ObservableListWrapper<>(new ArrayList<>());
        this.options = Options.DEFAULT;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.GID.toString(), gid);
        obj.addProperty(Fields.STATUS.toString(), status.val);
        obj.add(Fields.HOST.toString(), host.toJson());
        obj.add(Fields.OPTIONS.toString(), options.toJson());

        JsonArray players = new JsonArray();
        for (Player player : this.players) players.add(player.toJson());
        obj.add(Fields.PLAYERS.toString(), players);

        JsonArray spectators = new JsonArray();
        for (User spectator : this.spectators) spectators.add(spectator.toJson());
        obj.add(Fields.SPECTATORS.toString(), spectators);

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
        LOBBY("l"),
        JUDGING("j"),
        PLAYING("p");

        private final String val;

        Status(String val) {
            this.val = val;
        }

        public static Status parse(String val) {
            for (Status status : values())
                if (Objects.equals(status.val, val))
                    return status;

            return null;
        }

        @Override
        public String toString() {
            return val;
        }
    }

    public static class Options implements Jsonable {
        private static final Options DEFAULT = new Options(10, 10, new ArrayList<>());
        public final int maxPlayers;
        public final int maxSpectators;
        public final List<Integer> cardSetIds;

        public Options(int maxPlayers, int maxSpectators, List<Integer> cardSetIds) {
            this.maxPlayers = maxPlayers;
            this.maxSpectators = maxSpectators;
            this.cardSetIds = cardSetIds;
        }

        @Override
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty(Fields.MAX_PLAYERS.toString(), maxPlayers);
            obj.addProperty(Fields.MAX_SPECTATORS.toString(), maxSpectators);
            obj.add(Fields.CARD_SET_ID.toString(), Utils.toJsonArray(cardSetIds));
            return obj;
        }
    }
}
