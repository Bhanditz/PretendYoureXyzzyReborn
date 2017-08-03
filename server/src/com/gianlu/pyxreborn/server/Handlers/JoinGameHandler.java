package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class JoinGameHandler extends BaseHandlerWithUser {
    public JoinGameHandler() {
        super(Operations.JOIN_GAME);
    }

    @Override
    public JsonObject handleRequest(Server server, @NotNull User user, JsonObject request, JsonObject response) throws GeneralException {
        JsonElement gid = request.get(Fields.GID.toString());
        if (gid == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        Game game = server.games.findGameById(gid.getAsInt());
        if (game == null) throw new GeneralException(ErrorCodes.GAME_DOESNT_EXIST);
        server.games.joinGame(game, user);
        return successful(response);
    }
}
