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

    /**
     * Creates a new game.
     */
    public Game createAndAdd(@NotNull User host) throws GeneralException {
        if (size() >= maxGames) throw new GeneralException(ErrorCodes.TOO_MANY_GAMES);
        if (playingIn(host) != null || spectatingIn(host) != null)
            throw new GeneralException(ErrorCodes.ALREADY_IN_GAME);

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

    /**
     * Starts a game. Creating the {@link GameManager} if needed.
     */
    public void startGame(Game game) throws GeneralException {
        if (game.status != Game.Status.LOBBY) throw new GeneralException(ErrorCodes.GAME_ALREADY_STARTED);
        GameManager manager = managedGames.findGameManagerByGameId(game.gid);
        if (manager == null) {
            manager = new GameManager(server, game);
            manager.start();
            managedGames.add(manager);
        } else {
            manager.start();
        }
    }

    @Nullable
    private Game spectatingIn(User user) {
        for (Game game : this)
            if (game.spectators.contains(user))
                return game;

        return null;
    }

    @Nullable
    public Game playingIn(User user) {
        for (Game game : this)
            for (Player player : game.players)
                if (Objects.equals(player.user, user))
                    return game;

        return null;
    }

    /**
     * Removes a player or a spectator from the game.
     */
    public void leaveGame(@NotNull Game game, @NotNull User user) {
        for (Player player : new ArrayList<>(game.players)) {
            if (Objects.equals(player.user, user)) {
                game.players.remove(player);

                JsonObject obj = Utils.event(Events.GAME_PLAYER_LEFT);
                obj.addProperty(Fields.NICKNAME.toString(), user.nickname);
                server.broadcastMessageToPlayers(game, obj);  // Don't broadcast this to the player itself

                if (user == game.host && !game.players.isEmpty())
                    game.host = game.players.get(0).user;

                if (game.players.isEmpty()) killGame(game, KickReason.GAME_EMPTY);
                return;
            }
        }

        for (User spectator : new ArrayList<>(game.spectators)) {
            if (Objects.equals(spectator, user)) {
                game.spectators.remove(spectator);

                JsonObject obj = Utils.event(Events.GAME_SPECTATOR_LEFT);
                obj.addProperty(Fields.NICKNAME.toString(), user.nickname);
                server.broadcastMessageToPlayers(game, obj);  // Don't broadcast this to the user itself
                return;
            }
        }
    }

    /**
     * Kills the game, kicking everyone and deleting the game
     */
    @AdminOnly
    public void killGame(@NotNull Game game, KickReason majorReason) {
        GameManager manager = managedGames.findGameManagerByGameId(game.gid);
        if (manager != null) {
            try {
                manager.stop();
            } catch (GeneralException ignored) {
            }
        }

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

        if (manager != null) managedGames.remove(manager);
        remove(game);

        server.broadcastMessage(Utils.event(Events.GAME_REMOVED));
    }

    /**
     * Kicks a player from the game.
     */
    public void kickPlayer(@NotNull Game game, @NotNull Player user, KickReason reason) {
        for (Player player : game.players) {
            if (Objects.equals(player, user)) {
                game.players.remove(user);

                JsonObject obj = new JsonObject();
                obj.addProperty(Fields.KICKED.toString(), reason.toString());
                server.sendMessage(user.user, obj);
            }
        }
    }

    /**
     * Kicks a spectator from the game.
     */
    public void kickSpectator(@NotNull Game game, @NotNull User user, KickReason reason) {
        for (User spectator : game.spectators) {
            if (Objects.equals(spectator, user)) {
                game.spectators.remove(user);

                JsonObject obj = new JsonObject();
                obj.addProperty(Fields.KICKED.toString(), reason.toString());
                server.sendMessage(user, obj);
            }
        }
    }

    /**
     * Adds a player to the game.
     */
    public void joinGame(@NotNull Game game, @NotNull User user) throws GeneralException {
        if (playingIn(user) != null || spectatingIn(user) != null)
            throw new GeneralException(ErrorCodes.ALREADY_IN_GAME);
        if (game.players.size() >= game.options.maxPlayers) throw new GeneralException(ErrorCodes.GAME_FULL);

        Player player = new Player(user);

        JsonObject obj = Utils.event(Events.GAME_NEW_PLAYER);
        obj.add(Fields.PLAYER.toString(), player.toJson());
        server.broadcastMessageToPlayers(game, obj); // Don't broadcast this to the player itself

        game.players.add(player);
    }

    /**
     * Adds a spectator to the game.
     */
    public void spectateGame(@NotNull Game game, @NotNull User user) throws GeneralException {
        if (playingIn(user) != null || spectatingIn(user) != null)
            throw new GeneralException(ErrorCodes.ALREADY_IN_GAME);
        if (game.spectators.size() >= game.options.maxSpectators) throw new GeneralException(ErrorCodes.GAME_FULL);

        JsonObject obj = Utils.event(Events.GAME_NEW_SPECTATOR);
        obj.add(Fields.SPECTATOR.toString(), user.toJson());
        server.broadcastMessageToPlayers(game, obj); // Don't broadcast this to the user itself

        game.spectators.add(user);
    }

    /**
     * Changes the game options. Partial updates are supported.
     */
    public void changeGameOptions(@NotNull Game game, @NotNull JsonObject options) throws GeneralException {
        if (game.status != Game.Status.LOBBY) throw new GeneralException(ErrorCodes.GAME_ALREADY_STARTED);

        game.options = new Game.Options(
                options.has(Fields.MAX_PLAYERS.toString()) ? options.get(Fields.MAX_PLAYERS.toString()).getAsInt() : game.options.maxPlayers,
                options.has(Fields.MAX_SPECTATORS.toString()) ? options.get(Fields.MAX_SPECTATORS.toString()).getAsInt() : game.options.maxSpectators,
                options.has(Fields.CARD_SET_ID.toString()) ? Utils.toIntegersList(options.get(Fields.CARD_SET_ID.toString()).getAsJsonArray()) : game.options.cardSetIds);

        JsonObject obj = Utils.event(Events.GAME_OPTIONS_CHANGED);
        obj.add(Fields.OPTIONS.toString(), game.options.toJson());
        server.broadcastMessageToPlayers(game, obj);
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
