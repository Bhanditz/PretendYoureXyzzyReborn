package com.gianlu.pyxreborn.server.DB;

import com.gianlu.pyxreborn.Models.BlackCard;
import com.gianlu.pyxreborn.Models.CardSet;
import com.gianlu.pyxreborn.Models.WhiteCard;
import com.gianlu.pyxreborn.server.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardsDB {
    private final Connection db;

    public CardsDB(Config config) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        db = DriverManager.getConnection("jdbc:sqlite:" + config.cardsDatabase.getAbsolutePath());
        db.setAutoCommit(false);
    }

    private Map<Integer, WhiteCard> loadWhiteCards(Statement statement) throws SQLException {
        Map<Integer, WhiteCard> whiteCards = new HashMap<>();
        try (ResultSet result = statement.executeQuery("SELECT * FROM white_cards")) {
            while (result.next()) {
                WhiteCard card = new WhiteCard(
                        result.getInt("id"),
                        result.getString("text"),
                        result.getString("watermark"));
                whiteCards.put(card.id, card);
            }
        }
        return whiteCards;
    }

    private Map<Integer, BlackCard> loadBlackCards(Statement statement) throws SQLException {
        Map<Integer, BlackCard> blackCards = new HashMap<>();
        try (ResultSet result = statement.executeQuery("SELECT * FROM black_cards")) {
            while (result.next()) {
                BlackCard card = new BlackCard(
                        result.getInt("id"),
                        result.getString("text"),
                        result.getString("watermark"),
                        result.getInt("draw"),
                        result.getInt("pick"));
                blackCards.put(card.id, card);
            }
        }
        return blackCards;
    }

    public List<CardSet> loadCardSets() throws SQLException {
        List<CardSet> sets = new ArrayList<>();
        try (Statement statement = db.createStatement()) {
            try (ResultSet result = statement.executeQuery("SELECT * FROM card_set")) {
                while (result.next()) {
                    sets.add(new CardSet(
                            result.getInt("id"),
                            result.getInt("active") == 1,
                            result.getString("name"),
                            result.getInt("base_deck") == 1,
                            result.getString("description"),
                            result.getInt("weight")));
                }
            }

            Map<Integer, WhiteCard> whiteCards = loadWhiteCards(statement);
            for (CardSet set : sets) {
                try (ResultSet result = statement.executeQuery("SELECT white_card_id FROM card_set_white_card WHERE card_set_id=" + set.id)) {
                    while (result.next()) {
                        set.whiteCards.add(whiteCards.get(result.getInt("white_card_id")));
                    }
                }
            }

            Map<Integer, BlackCard> blackCards = loadBlackCards(statement);
            for (CardSet set : sets) {
                try (ResultSet result = statement.executeQuery("SELECT black_card_id FROM card_set_black_card WHERE card_set_id=" + set.id)) {
                    while (result.next()) {
                        set.blackCards.add(blackCards.get(result.getInt("black_card_id")));
                    }
                }
            }
        }

        return sets;
    }
}
