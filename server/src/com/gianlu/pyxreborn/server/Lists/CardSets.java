package com.gianlu.pyxreborn.server.Lists;

import com.gianlu.pyxreborn.Models.CardSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CardSets extends ArrayList<CardSet> {

    @Nullable
    public CardSet findCardSetById(int id) {
        for (CardSet set : this)
            if (set.id == id)
                return set;

        return null;
    }
}
