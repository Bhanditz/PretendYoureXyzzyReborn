package com.gianlu.pyxreborn.server.Handlers;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Server;
import com.google.gson.JsonObject;

public abstract class BaseHandler {
    public final Operations OP;

    public BaseHandler(Operations op) {
        this.OP = op;
    }

    public abstract JsonObject handleRequest(Server server, JsonObject request, JsonObject response) throws GeneralException;
}
