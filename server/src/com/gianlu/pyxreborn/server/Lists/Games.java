package com.gianlu.pyxreborn.server.Lists;

import com.gianlu.pyxreborn.Annotations.AdminOnly;
import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.KickReason;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.Player;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.server.GameManager;
import com.gianlu.pyxreborn.server.PyxServerAdapter;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Games extends ArrayList<Game> {
    private final PyxServerAdapter server;
    private final int maxGames;
    private final ManagedGames managedGames;

    public Games(PyxServerAdapter server) {
        this.server = server;
        this.maxGames = server.config.maxGames;
        this.managedGames = new ManagedGames();
    }

    public int getMax() {
        return maxGames;
    }

    public Game createAndAdd(@NotNull User host) throws GeneralException {
        if (size() >= maxGames) throw new GeneralException(ErrorCodes.TOO_MANY_GAMES);
        if (playingIn(host) != null) throw new GeneralException(ErrorCodes.ALREADY_IN_GAME);

        Game game = new Game(new Random().nextInt(), host);
        game.players.add(new Player(host));
        add(game);

        JsonObject obj = Utils.event(Events.NEW_GAME);
        obj.addProperty(Fields.GID.toString(), game.gid);
        server.broadcastMessage(obj);

        return game;
    }

    @Nullable
    public GameManager getGameManagerFor(int gid) {
        return managedGames.findGameManagerByGameId(gid);
    }

    public void startGame(Game game) throws GeneralException {
        GameManager manager = new GameManager(server, game);
        manager.start();
        managedGames.add(manager);
    }

    @Nullable
    public Game playingIn(User user) {
        for (Game game : this)
            for (Player player : game.players)
                if (Objects.equals(player.user, user))
                    return game;

        return null;
    }

    public void leaveGame(@NotNull Game game, @NotNull User user) {
        for (Player player : new ArrayList<>(game.players)) {
            if (Objects.equals(player.user, user)) {
                JsonObject obj = Utils.event(Events.GAME_PLAYER_LEFT);
                obj.add(Fields.USER.toString(), user.toJson());
                server.broadcastMessageToPlayers(game, obj);  // Don't broadcast this to the player itself

                game.players.remove(player);
                killGameIfEmpty(game);
            }
        }

        // The player wasn't there
    }

    /**
     * This kills the game if there are no more players
     */
    private void killGameIfEmpty(@NotNull Game game) {
        if (game.players.isEmpty()) killGame(game, KickReason.GAME_EMPTY);
    }

    /**
     * This kills the game, kicking everyone and deleting the game
     */
    @AdminOnly
    public void killGame(@NotNull Game game, KickReason majorReason) {
        if (!game.players.isEmpty()) {
            for (Player player : game.players) {
                kickPlayer(game, player, majorReason);
            }
        }

        if (!game.spectators.isEmpty()) {
            for (User spectator : game.spectators) {
                kickSpectator(game, spectator, majorReason);
            }
        }

        remove(game);

        server.broadcastMessage(Utils.event(Events.GAME_REMOVED));
    }

    public void kickPlayer(@NotNull Game game, @NotNull Player user, KickReason reason) {
        for (Player player : game.players) {
            if (Objects.equals(player, user)) {
                game.players.remove(user);

                JsonObject obj = new JsonObject();
                obj.addProperty(Fields.KICKED.toString(), reason.toString());
                server.sendMessage(user.user, obj);
            }
        }

        // The player wasn't there
    }

    public void kickSpectator(@NotNull Game game, @NotNull User user, KickReason reason) {
        for (User spectator : game.spectators) {
            if (Objects.equals(spectator, user)) {
                game.spectators.remove(user);

                JsonObject obj = new JsonObject();
                obj.addProperty(Fields.KICKED.toString(), reason.toString());
                server.sendMessage(user, obj);
            }
        }

        // The spectator wasn't there
    }

    public void joinGame(@NotNull Game game, @NotNull User user) throws GeneralException {
        if (playingIn(user) != null) throw new GeneralException(ErrorCodes.ALREADY_IN_GAME);
        if (game.players.size() >= game.options.maxPlayers) throw new GeneralException(ErrorCodes.GAME_FULL);

        JsonObject obj = Utils.event(Events.GAME_NEW_PLAYER);
        obj.add(Fields.USER.toString(), user.toJson());
        server.broadcastMessageToPlayers(game, obj); // Don't broadcast this to the player itself

        game.players.add(new Player(user));
    }

    public void changeGameOptions(@NotNull Game game, JsonObject request) throws GeneralException {
        if (game.status != Game.Status.LOBBY) throw new GeneralException(ErrorCodes.GAME_ALREADY_STARTED);

        game.options = new Game.Options(
                request.has(Fields.MAX_PLAYERS.toString()) ? request.get(Fields.MAX_PLAYERS.toString()).getAsInt() : game.options.maxPlayers,
                request.has(Fields.MAX_SPECTATORS.toString()) ? request.get(Fields.MAX_SPECTATORS.toString()).getAsInt() : game.options.maxSpectators,
                request.has(Fields.CARD_SET_ID.toString()) ? Utils.toIntegersList(request.get(Fields.CARD_SET_ID.toString()).getAsString()) : game.options.cardSetIds);
    }

    @Nullable
    public Game findGameById(int gid) {
        for (Game game : this)
            if (game.gid == gid)
                return game;

        return null;
    }

    private class ManagedGames extends ArrayList<GameManager> {

        @Nullable
        public GameManager findGameManagerByGameId(int gid) {
            for (GameManager game : this)
                if (game.game.gid == gid)
                    return game;

            return null;
        }
    }
}
