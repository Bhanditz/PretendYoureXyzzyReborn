package com.gianlu.pyxreborn.Models;

import java.util.ArrayList;
import java.util.List;

public class CardSet {
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
}
