package com.gianlu.pyxreborn.server.Lists;

import com.gianlu.pyxreborn.Models.CardSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;

public class CardSets extends ArrayList<CardSet> {

    public static class WeightComparator implements Comparator<CardSet> {

        @Override
        public int compare(CardSet o1, CardSet o2) {
            return o1.weight - o2.weight;
        }
    }

    @Nullable
    public CardSet findCardSetById(int id) {
        for (CardSet set : this)
            if (set.id == id)
                return set;

        return null;
    }
}
