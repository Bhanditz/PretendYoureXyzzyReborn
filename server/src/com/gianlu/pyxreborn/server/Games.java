package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Models.Game;

import java.util.ArrayList;

public class Games extends ArrayList<Game> {
    private final int maxGames;

    public Games(int maxGames) {
        this.maxGames = maxGames;
    }

    public int getMax() {
        return maxGames;
    }
}
