package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public abstract class BaseHandlerWithUser extends BaseHandler {

    public BaseHandlerWithUser(Operations op) {
        super(op);
    }

    public abstract JsonObject handleRequest(Server server, @NotNull User user, JsonObject request, JsonObject response) throws GeneralException;

    @Override
    public final JsonObject handleRequest(Server server, JsonObject request, JsonObject response) throws GeneralException {
        JsonElement sid = request.get(Fields.SESSION_ID.toString());
        if (sid == null) throw new GeneralException(ErrorCodes.INVALID_REQUEST);
        User user = server.users.findBySessionId(sid.getAsString());
        if (user == null) throw new GeneralException(ErrorCodes.NOT_CONNECTED);
        return handleRequest(server, user, request, response);
    }
}
