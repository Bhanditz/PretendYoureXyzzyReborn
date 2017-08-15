package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class CardSet implements Jsonable {
    public final int id;
    public final boolean active;
    public final String name;
    public final boolean baseDeck;
    public final String description;
    public final int weight;
    public final List<WhiteCard> whiteCards;
    public final List<BlackCard> blackCards;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardSet cardSet = (CardSet) o;
        return id == cardSet.id;
    }

    public CardSet(int id, boolean active, String name, boolean baseDeck, String description, int weight) {
        this.id = id;
        this.active = active;
        this.name = name;
        this.baseDeck = baseDeck;
        this.description = description;
        this.weight = weight;
        this.whiteCards = new ArrayList<>();
        this.blackCards = new ArrayList<>();
    }

    public JsonObject toCompactJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.CARD_SET_ID.toString(), id);
        obj.addProperty(Fields.NAME.toString(), name);
        obj.addProperty(Fields.DESCRIPTION.toString(), description);
        obj.addProperty(Fields.WEIGHT.toString(), weight);
        return obj;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = toCompactJson();
        JsonArray whiteArray = new JsonArray();
        for (WhiteCard card : whiteCards) whiteArray.add(card.toJson());
        obj.add(Fields.WHITE_CARD.toString(), whiteArray);
        JsonArray blackArray = new JsonArray();
        for (BlackCard card : blackCards) blackArray.add(card.toJson());
        obj.add(Fields.BLACK_CARD.toString(), blackArray);
        return obj;
    }
}
