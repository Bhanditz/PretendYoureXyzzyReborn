package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonObject;

public class CreateGameHandler extends BaseHandlerWithUser {
    public CreateGameHandler() {
        super(Operations.CREATE_GAME);
    }

    @Override
    public JsonObject handleRequest(Server server, User user, JsonObject request, JsonObject response) throws GeneralException {
        Game game = server.games.createAndAdd(user);
        response.addProperty(Fields.GID.toString(), game.gid);
        return response;
    }
}
