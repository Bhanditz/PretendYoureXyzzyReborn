package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public abstract class BaseHandlerWithAdminUser extends BaseHandlerWithUser {
    public BaseHandlerWithAdminUser(Operations op) {
        super(op);
    }

    public abstract JsonObject handleRequestForAdmin(Server server, @NotNull User user, JsonObject request, JsonObject response) throws GeneralException;

    @Override
    public JsonObject handleRequest(Server server, @NotNull User user, JsonObject request, JsonObject response) throws GeneralException {
        if (!user.isAdmin) throw new GeneralException(ErrorCodes.NOT_ADMIN);
        return handleRequestForAdmin(server, user, request, response);
    }
}
