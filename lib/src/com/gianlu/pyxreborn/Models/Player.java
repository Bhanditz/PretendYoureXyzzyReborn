package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Player implements Jsonable {
    public final User user;
    public final Hand hand;

    public Player(@NotNull User user) {
        this.user = user;
        this.hand = new Hand();
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.add(Fields.USER.toString(), user.toJson());
        return obj;
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
