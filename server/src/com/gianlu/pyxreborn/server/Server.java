package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.server.Handlers.BaseHandler;
import com.gianlu.pyxreborn.server.Handlers.Handlers;
import com.gianlu.pyxreborn.server.Lists.CardSets;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

public class Server extends PyxServerAdapter {

    public Server(Config config, CardSets sets) {
        super(config, sets);
    }

    @Override
    @Nullable
    protected JsonObject onMessage(WebSocket conn, User user, JsonObject request, JsonObject response) throws GeneralException {
        Operations op = Operations.parse(request.get(Fields.OPERATION.toString()).getAsString());
        if (op == null) throw new GeneralException(ErrorCodes.UNKNOWN_OPERATION);

        BaseHandler handler;
        try {
            handler = Handlers.LIST.get(op).newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new GeneralException(ErrorCodes.SERVER_ERROR, ex);
        }

        return handler.handleRequest(this, request, response);
    }
}
