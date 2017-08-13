package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.CardSet;
import com.gianlu.pyxreborn.Models.Game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final static int MIN_WHITE_CARDS = 50;
    private final static int MIN_BLACK_CARDS = 10;
    public final Game game;
    private final PyxServerAdapter server;
    private final List<CardSet> cardSets;

    public GameManager(PyxServerAdapter server, Game game) {
        this.server = server;
        this.game = game;
        this.cardSets = new ArrayList<>();
    }

    private void loadCards() throws GeneralException {
        cardSets.clear();
        List<Integer> cardSetIds = game.options.cardSetIds;
        if (cardSetIds.isEmpty()) throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_CARDS);

        for (int id : cardSetIds) {
            CardSet set = server.cardSets.findCardSetById(id);
            if (set != null) cardSets.add(set);
        }

        int whiteCards = 0;
        int blackCards = 0;
        for (CardSet set : cardSets) {
            whiteCards += set.whiteCards.size();
            blackCards += set.blackCards.size();
        }

        if (whiteCards < MIN_WHITE_CARDS) throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_CARDS);
        if (blackCards < MIN_BLACK_CARDS) throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_CARDS);
    }

    public void start() throws GeneralException {
        if (game.players.size() < 3) throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_PLAYERS);
        loadCards();

        // TODO: Start game
    }
}
