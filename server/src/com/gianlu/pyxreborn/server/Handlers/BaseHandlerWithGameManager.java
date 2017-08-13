package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.GameManager;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public abstract class BaseHandlerWithGameManager extends BaseHandlerWithUser {
    public BaseHandlerWithGameManager(Operations op) {
        super(op);
    }

    public abstract JsonObject handleRequest(Server server, @NotNull User user, @NotNull GameManager manager, JsonObject request, JsonObject response) throws GeneralException;

    @Override
    public JsonObject handleRequest(Server server, @NotNull User user, JsonObject request, JsonObject response) throws GeneralException {
        JsonElement gid = request.get(Fields.GID.toString());
        if (gid == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        GameManager manager = server.games.getGameManagerFor(gid.getAsInt());
        if (manager == null) throw new GeneralException(ErrorCodes.GAME_NOT_STARTED);
        return handleRequest(server, user, manager, request, response);
    }
}
