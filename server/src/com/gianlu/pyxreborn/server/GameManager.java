package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.*;
import com.gianlu.pyxreborn.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameManager {
    private static final int CARDS_PER_HAND = 10;
    public final Game game;
    private final int intermission;
    private final PyxServerAdapter server;
    private final List<CardSet> cardSets;
    private final List<WhiteCard> whiteCards;
    private final List<BlackCard> blackCards;
    private final Random random = new Random();
    private final Timer generalTimer = new Timer();
    private Round round;

    public GameManager(PyxServerAdapter server, Game game) {
        this.server = server;
        this.game = game;
        this.intermission = server.config.intermission * 1000;
        this.cardSets = new ArrayList<>();
        this.whiteCards = new ArrayList<>();
        this.blackCards = new ArrayList<>();
    }

    private void loadCards() throws GeneralException {
        cardSets.clear();
        whiteCards.clear();
        blackCards.clear();

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

        if (whiteCards <= game.players.size() * CARDS_PER_HAND)
            throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_CARDS);
        if (blackCards <= game.players.size())
            throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_CARDS);

        for (CardSet set : cardSets) {
            this.whiteCards.addAll(set.whiteCards);
            this.blackCards.addAll(set.blackCards);
        }
    }

    private void reloadWhiteCards() {
        whiteCards.clear();
        for (CardSet set : cardSets) whiteCards.addAll(set.whiteCards);
        for (Player player : game.players) whiteCards.removeAll(player.hand); // This way we don't have duplicated cards
    }

    private void handDeal() {
        for (Player player : game.players) {
            while (player.hand.size() < 10) {
                if (whiteCards.isEmpty()) reloadWhiteCards();
                player.hand.add(whiteCards.get(random.nextInt(whiteCards.size())));
            }

            JsonObject obj = Utils.event(Events.GAME_HAND_CHANGED);
            JsonArray array = new JsonArray();
            for (WhiteCard card : player.hand) array.add(card.toJson());
            obj.add(Fields.HAND.toString(), array);
            server.sendMessage(player.user, obj);
        }
    }

    private void _nextRound() {
        handDeal();
        round = new Round();
    }

    private void nextRound() {
        if (round == null) {
            _nextRound();
        } else {
            generalTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    _nextRound();
                }
            }, intermission);
        }
    }

    public void start() throws GeneralException {
        if (game.players.size() < 3) throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_PLAYERS);
        loadCards();

        game.status = Game.Status.PLAYING;
        nextRound();
    }

    private void reloadBlackCards() {
        blackCards.clear();
        for (CardSet set : cardSets) blackCards.addAll(set.blackCards);
    }

    public void playCard(@NotNull Player player, int whiteCardId) throws GeneralException {
        if (round == null) throw new GeneralException(ErrorCodes.GAME_NOT_STARTED);
        WhiteCard card = player.hand.findCardById(whiteCardId);
        if (card == null) throw new GeneralException(ErrorCodes.GAME_CARD_NOT_IN_YOUR_HAND);
        round.playCard(player, card);
    }

    public void judge(@NotNull Player player, int whiteCardId) throws GeneralException {
        if (round == null) throw new GeneralException(ErrorCodes.GAME_NOT_STARTED);
        WhiteCard card = round.playedCards.findCardById(whiteCardId);
        if (card == null) throw new GeneralException(ErrorCodes.GAME_CARD_NOT_PLAYED);
        round.judge(player, card);
    }

    private class Round {
        private final PlayedCards playedCards;
        private int judgeIndex;
        private BlackCard blackCard;

        Round() {
            nextJudge();
            nextBlackCard();

            playedCards = new PlayedCards();

            JsonObject obj = Utils.event(Events.GAME_NEW_ROUND);
            obj.add(Fields.JUDGE.toString(), getJudge().toJson());
            obj.add(Fields.BLACK_CARD.toString(), blackCard.toJson());
            server.broadcastMessageToPlayers(game, obj);
        }

        private void playCard(@NotNull Player player, @NotNull WhiteCard card) throws GeneralException {
            if (player == getJudge()) throw new GeneralException(ErrorCodes.GAME_NOT_YOUR_TURN);
            playedCards.play(player, card);
            if (playedCards.everyonePlayed()) showCards();
        }

        private void judge(@NotNull Player judge, @NotNull WhiteCard card) throws GeneralException {
            if (judge != getJudge()) throw new GeneralException(ErrorCodes.GAME_NOT_YOUR_TURN);
            Player winner = playedCards.findPlayerByCard(card);
            if (winner == null) {
                // TODO: What?!
                return;
            }

            JsonObject obj = Utils.event(Events.GAME_ROUND_ENDED);
            obj.add(Fields.WINNER.toString(), winner.toJson());
            obj.addProperty(Fields.WINNER_CARD_ID.toString(), card.id);
            server.broadcastMessageToPlayers(game, obj);

            nextRound();
        }

        private void showCards() {
            JsonObject obj = Utils.event(Events.GAME_JUDGING);
            JsonArray array = new JsonArray();
            List<List<WhiteCard>> played = new ArrayList<>(playedCards.values());
            Collections.shuffle(played, random);
            for (List<WhiteCard> cards : played) {
                JsonArray subArray = new JsonArray();
                for (WhiteCard card : cards) subArray.add(card.toJson());
                array.add(subArray);
            }
            obj.add(Fields.PLAYED_CARDS.toString(), array);
            server.broadcastMessageToPlayers(game, obj);
        }

        private Player getJudge() {
            return game.players.get(judgeIndex);
        }

        private void nextJudge() {
            if (judgeIndex == game.players.size() - 1) judgeIndex = 0;
            else judgeIndex++;
        }

        private void nextBlackCard() {
            if (blackCards.isEmpty()) reloadBlackCards();
            blackCard = blackCards.get(random.nextInt(blackCards.size()));
        }

        private class PlayedCards extends HashMap<Player, List<WhiteCard>> {

            PlayedCards() {
                for (Player player : game.players)
                    if (player != getJudge())
                        playedCards.put(player, null);
            }

            private void play(Player player, WhiteCard card) {
                List<WhiteCard> cards = get(player);
                if (cards == null) cards = new ArrayList<>();
                cards.add(card);
            }

            @Nullable
            private Player findPlayerByCard(WhiteCard card) {
                for (Map.Entry<Player, List<WhiteCard>> entry : entrySet())
                    if (entry.getValue().contains(card))
                        return entry.getKey();

                return null;
            }

            @Nullable
            private WhiteCard findCardById(int id) {
                for (List<WhiteCard> cards : values())
                    for (WhiteCard card : cards)
                        if (card.id == id)
                            return card;

                return null;
            }

            private boolean everyonePlayed() {
                for (List<WhiteCard> cards : values())
                    if (cards == null || cards.size() < blackCard.numPick)
                        return false;

                return true;
            }
        }
    }
}
