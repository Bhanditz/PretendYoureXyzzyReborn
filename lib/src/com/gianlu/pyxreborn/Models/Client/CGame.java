package com.gianlu.pyxreborn.Models.Client;

import com.gianlu.pyxreborn.Annotations.ClientSafe;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.Jsonable;
import com.gianlu.pyxreborn.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

@ClientSafe
public class CGame implements Jsonable {
    public final int gid;
    public final Game.Status status;
    public final CUser host;
    public final Options options;
    public final List<CPlayer> players;
    public final List<CUser> spectators;

    public CGame(JsonObject obj) {
        gid = obj.get(Fields.GID.toString()).getAsInt();
        status = Game.Status.parse(obj.get(Fields.STATUS.toString()).getAsString());
        host = new CUser(obj.getAsJsonObject(Fields.HOST.toString()));
        options = new Options(obj.getAsJsonObject(Fields.OPTIONS.toString()));
        players = Utils.toList(obj.getAsJsonArray(Fields.PLAYERS.toString()), CPlayer.class);
        spectators = Utils.toList(obj.getAsJsonArray(Fields.SPECTATORS.toString()), CUser.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CGame game = (CGame) o;
        return gid == game.gid;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.GID.toString(), gid);
        obj.addProperty(Fields.STATUS.toString(), status.toString());
        obj.add(Fields.HOST.toString(), host.toJson());
        obj.add(Fields.OPTIONS.toString(), options.toJson());

        JsonArray players = new JsonArray();
        for (CPlayer player : this.players) players.add(player.toJson());
        obj.add(Fields.PLAYERS.toString(), players);

        JsonArray spectators = new JsonArray();
        for (CUser spectator : this.spectators) spectators.add(spectator.toJson());
        obj.add(Fields.SPECTATORS.toString(), spectators);

        return obj;
    }

    public static class Options implements Jsonable {
        public final int maxPlayers;
        public final int maxSpectators;
        public final List<Integer> cardSetIds;

        public Options(int maxPlayers, int maxSpectators, List<Integer> cardSetIds) {
            this.maxPlayers = maxPlayers;
            this.maxSpectators = maxSpectators;
            this.cardSetIds = cardSetIds;
        }

        public Options(JsonObject obj) {
            maxPlayers = obj.get(Fields.MAX_PLAYERS.toString()).getAsInt();
            maxSpectators = obj.get(Fields.MAX_SPECTATORS.toString()).getAsInt();
            cardSetIds = Utils.toIntegersList(obj.getAsJsonArray(Fields.CARD_SET_ID.toString()));
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
