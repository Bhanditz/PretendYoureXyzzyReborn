package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class JoinGameHandler extends BaseHandlerWithGame {
    public JoinGameHandler() {
        super(Operations.JOIN_GAME);
    }

    @Override
    public JsonObject handleRequest(Server server, @NotNull User user, @NotNull Game game, JsonObject request, JsonObject response) throws GeneralException {
        server.games.joinGame(game, user);
        return successful(response);
    }
}
