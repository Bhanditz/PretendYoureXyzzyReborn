package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class StartGameHandler extends BaseHandlerWithGame {
    public StartGameHandler() {
        super(Operations.START_GAME);
    }

    @Override
    public JsonObject handleRequest(Server server, @NotNull User user, @NotNull Game game, JsonObject request, JsonObject response) throws GeneralException {
        if (game.host != user) throw new GeneralException(ErrorCodes.NOT_GAME_HOST);
        server.games.startGame(game);
        return successful(response);
    }
}
