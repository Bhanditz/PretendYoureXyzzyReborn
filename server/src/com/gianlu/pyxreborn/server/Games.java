package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Annotations.AdminOnly;
import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.KickReason;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.Player;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Games extends ArrayList<Game> {
    private final PyxServerAdapter server;
    private final int maxGames;

    public Games(PyxServerAdapter server, int maxGames) {
        this.server = server;
        this.maxGames = maxGames;
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

        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.EVENT.toString(), Events.NEW_GAME.toString());
        obj.addProperty(Fields.GID.toString(), game.gid);
        server.broadcastMessage(obj);

        return game;
    }

    @Nullable
    public Game playingIn(User user) {
        for (Game game : this)
            for (Player player : game.players)
                if (Objects.equals(player.getUser(), user))
                    return game;

        return null;
    }

    public void leaveGame(@NotNull Game game, @NotNull User user) {
        for (Player player : new ArrayList<>(game.players)) {
            if (Objects.equals(player.getUser(), user)) {
                JsonObject obj = new JsonObject();
                obj.addProperty(Fields.EVENT.toString(), Events.GAME_PLAYER_LEFT.toString());
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

        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.EVENT.toString(), Events.GAME_REMOVED.toString());
        server.broadcastMessage(obj);
    }

    public void kickPlayer(@NotNull Game game, @NotNull Player user, KickReason reason) {
        for (Player player : game.players) {
            if (Objects.equals(player, user)) {
                game.players.remove(user);

                JsonObject obj = new JsonObject();
                obj.addProperty(Fields.KICKED.toString(), reason.toString());
                server.sendMessage(user.getUser(), obj);
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

        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.EVENT.toString(), Events.GAME_NEW_PLAYER.toString());
        obj.add(Fields.USER.toString(), user.toJson());
        server.broadcastMessageToPlayers(game, obj); // Don't broadcast this to the player itself

        game.players.add(new Player(user));
    }

    @Nullable
    public Game findGameById(int gid) {
        for (Game game : this)
            if (game.gid == gid)
                return game;

        return null;
    }
}
