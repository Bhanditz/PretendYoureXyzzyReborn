package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonObject;

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

    public Game createAndAdd(User host) throws GeneralException {
        if (size() >= maxGames) throw new GeneralException(ErrorCodes.TOO_MANY_GAMES);
        else if (isPlaying(host)) throw new GeneralException(ErrorCodes.ALREADY_IN_GAME);

        Game game = new Game(new Random().nextInt(), host);
        game.players.add(host);
        add(game);

        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.EVENT.toString(), Events.NEW_GAME.toString());
        obj.addProperty(Fields.GID.toString(), game.gid);
        server.broadcastMessage(obj);

        return game;
    }

    public boolean isPlaying(User user) {
        for (Game game : this)
            for (User player : game.players)
                if (Objects.equals(player, user))
                    return true;

        return false;
    }
}
