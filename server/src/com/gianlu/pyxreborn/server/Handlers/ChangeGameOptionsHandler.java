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

public class ChangeGameOptionsHandler extends BaseHandlerWithGame {
    public ChangeGameOptionsHandler() {
        super(Operations.CHANGE_GAME_OPTIONS);
    }

    @Override
    public JsonObject handleRequest(Server server, @NotNull User user, @NotNull Game game, JsonObject request, JsonObject response) throws GeneralException {
        if (user != game.host) throw new GeneralException(ErrorCodes.NOT_GAME_HOST);
        JsonElement options = request.get(Fields.OPTIONS.toString());
        if (options == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        server.games.changeGameOptions(game, options.getAsJsonObject());
        return successful(response);
    }
}
