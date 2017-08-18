package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class Player implements Jsonable {
    public final User user;
    public final Hand hand;
    public Status status = Status.WAITING;
    public int score = 0;

    public Player(@NotNull User user) {
        this.user = user;
        this.hand = new Hand();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
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

    public enum Status {
        WAITING("w"),
        PLAYING("p"),
        WAITING_JUDGE("wj"),
        JUDGING("j");

        private final String val;

        Status(String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return val;
        }

        @Nullable
        public static Status parse(String val) {
            for (Status status : values())
                if (Objects.equals(status.val, val))
                    return status;

            return null;
        }
    }

    public class Hand extends ArrayList<WhiteCard> {

        @Nullable
        public WhiteCard findCardById(int id) {
            for (WhiteCard card : this)
                if (card.id == id)
                    return card;

            return null;
        }
    }
}
