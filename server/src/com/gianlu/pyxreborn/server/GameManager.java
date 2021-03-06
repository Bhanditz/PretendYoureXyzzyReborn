package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.*;
import com.gianlu.pyxreborn.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.collections.ListChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameManager implements ListChangeListener<Player> {
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
    private int judgeIndex = 0;

    public GameManager(PyxServerAdapter server, Game game) {
        this.server = server;
        this.game = game;
        this.intermission = server.config.intermission * 1000;
        this.cardSets = new ArrayList<>();
        this.whiteCards = new ArrayList<>();
        this.blackCards = new ArrayList<>();

        game.players.addListener(this);
    }

    /**
     * Load the needed cards.
     *
     * @throws GeneralException if the game can't be started with the current options.
     */
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

    /**
     * Reload the {@link #whiteCards} taking care of removing cards already present in the players' hands.
     */
    private void reloadWhiteCards() {
        whiteCards.clear();
        for (CardSet set : cardSets) whiteCards.addAll(set.whiteCards);
        for (Player player : game.players) whiteCards.removeAll(player.hand); // This way we don't have duplicated cards
    }

    /**
     * Restore each player hand to {@link #CARDS_PER_HAND} cards.
     */
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
        game.status = Game.Status.PLAYING;
        handDeal();
        round = new Round();
    }

    /**
     * Starts a new round if no round was made before or after {@link #intermission} if it's not the first round.
     */
    private void nextRound() {
        if (round == null) {
            _nextRound();
        } else {
            round = null;
            generalTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    _nextRound();
                }
            }, intermission);
        }
    }

    /**
     * Starts the game.
     *
     * @throws GeneralException if the can't be started.
     */
    public void start() throws GeneralException {
        if (game.players.size() < 3) throw new GeneralException(ErrorCodes.GAME_NOT_ENOUGH_PLAYERS);
        loadCards();

        nextRound();
    }

    /**
     * Stops the game.
     */
    public void stop() throws GeneralException {
        if (game.status == Game.Status.LOBBY) throw new GeneralException(ErrorCodes.GAME_NOT_STARTED);
        round = null;

        whiteCards.clear();
        blackCards.clear();
        cardSets.clear();

        game.status = Game.Status.LOBBY;
        server.broadcastMessageToPlayers(game, Utils.event(Events.GAME_STOPPED));
    }

    /**
     * Reloads {@link #blackCards}.
     */
    private void reloadBlackCards() {
        blackCards.clear();
        for (CardSet set : cardSets) blackCards.addAll(set.blackCards);
    }

    /**
     * Plays a card.
     */
    public void playCard(@NotNull Player player, int whiteCardId) throws GeneralException {
        if (game.status == Game.Status.LOBBY) throw new GeneralException(ErrorCodes.GAME_NOT_STARTED);
        if (round == null) throw new GeneralException(ErrorCodes.GAME_NOT_YOUR_TURN);
        WhiteCard card = player.hand.findCardById(whiteCardId);
        if (card == null) throw new GeneralException(ErrorCodes.GAME_CARD_NOT_IN_YOUR_HAND);
        round.playCard(player, card);
    }

    /**
     * Decides the winning card.
     */
    public void judge(@NotNull Player player, int whiteCardId) throws GeneralException {
        if (game.status == Game.Status.LOBBY) throw new GeneralException(ErrorCodes.GAME_NOT_STARTED);
        if (round == null) throw new GeneralException(ErrorCodes.GAME_NOT_YOUR_TURN);
        WhiteCard card = round.playedCards.findCardById(whiteCardId);
        if (card == null) throw new GeneralException(ErrorCodes.GAME_CARD_NOT_PLAYED);
        round.judge(player, card);
    }

    /**
     * Notifies that a player left the game.
     */
    private void onPlayerLeft() {
        try {
            if (game.players.size() < 3) stop(); // Stop the game if there are too less players
        } catch (GeneralException ignored) {
        }
    }

    @Override
    public void onChanged(Change<? extends Player> change) {
        while (change.next()) {
            if (change.getRemovedSize() > 0) onPlayerLeft();
        }
    }

    private class Round {
        private final PlayedCards playedCards;
        private BlackCard blackCard;

        /**
         * Starts a new round doing so:
         * <p>
         * - Selecting the judge
         * - Selecting the black card
         * <p>
         * Also takes care on restoring players statuses.
         */
        Round() {
            nextJudge();
            getJudge().status = Player.Status.WAITING_JUDGE;
            nextBlackCard();

            playedCards = new PlayedCards();

            for (Player player : game.players) {
                if (getJudge() == player) player.status = Player.Status.WAITING_JUDGE;
                else player.status = Player.Status.PLAYING;

                JsonObject obj = Utils.event(Events.GAME_PLAYER_STATUS_CHANGED);
                obj.add(Fields.PLAYER.toString(), player.toJson());
                server.broadcastMessageToPlayers(game, obj);
            }

            JsonObject obj = Utils.event(Events.GAME_NEW_ROUND);
            obj.add(Fields.JUDGE.toString(), getJudge().toJson());
            obj.add(Fields.BLACK_CARD.toString(), blackCard.toJson());
            server.broadcastMessageToPlayers(game, obj);
        }

        /**
         * Plays a card and checks if everyone played. Also notifies player status changed.
         */
        private void playCard(@NotNull Player player, @NotNull WhiteCard card) throws GeneralException {
            if (player == getJudge()) throw new GeneralException(ErrorCodes.GAME_NOT_YOUR_TURN);
            playedCards.play(player, card);
            player.hand.remove(card);

            List<WhiteCard> playedByUser = playedCards.get(player);
            if (playedByUser != null && playedByUser.size() == blackCard.numPick) {
                player.status = Player.Status.WAITING;
                JsonObject obj = Utils.event(Events.GAME_PLAYER_STATUS_CHANGED);
                obj.add(Fields.PLAYER.toString(), player.toJson());
                server.broadcastMessageToPlayers(game, obj);
            }

            if (playedCards.everyonePlayed()) showCards();
        }

        /**
         * Selects the winning card. Starts a new round after notifying the winner and the winning card.
         */
        private void judge(@NotNull Player judge, @NotNull WhiteCard card) throws GeneralException {
            if (judge != getJudge() || game.status != Game.Status.JUDGING)
                throw new GeneralException(ErrorCodes.GAME_NOT_YOUR_TURN);
            Player winner = playedCards.findPlayerByCard(card);
            if (winner == null) throw new GeneralException(ErrorCodes.NOT_IN_THIS_GAME);
            winner.score++;

            JsonObject obj = Utils.event(Events.GAME_ROUND_ENDED);
            obj.add(Fields.WINNER.toString(), winner.toJson());
            obj.addProperty(Fields.WINNER_CARD_ID.toString(), card.id);
            server.broadcastMessageToPlayers(game, obj);

            nextRound();
        }

        /**
         * Shows played cards to all the players.
         */
        private void showCards() {
            game.status = Game.Status.JUDGING;
            getJudge().status = Player.Status.JUDGING;
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

        /**
         * Selects the next judge.
         */
        private void nextJudge() {
            if (judgeIndex == game.players.size() - 1) judgeIndex = 0;
            else judgeIndex++;
        }

        /**
         * Selects the next black card.
         */
        private void nextBlackCard() {
            if (blackCards.isEmpty()) reloadBlackCards();
            blackCard = blackCards.get(random.nextInt(blackCards.size()));
        }

        private class PlayedCards extends HashMap<Player, List<WhiteCard>> {

            PlayedCards() {
                for (Player player : game.players)
                    if (player != getJudge())
                        put(player, null);
            }

            /**
             * Add a card to the player's played cards.
             */
            private void play(Player player, WhiteCard card) {
                List<WhiteCard> cards = get(player);
                if (cards == null) cards = new ArrayList<>();
                cards.add(card);
                put(player, cards);
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

            /**
             * @return true if everyone played the cards.
             */
            private boolean everyonePlayed() {
                for (List<WhiteCard> cards : values())
                    if (cards == null || cards.size() < blackCard.numPick)
                        return false;

                return true;
            }
        }
    }
}
