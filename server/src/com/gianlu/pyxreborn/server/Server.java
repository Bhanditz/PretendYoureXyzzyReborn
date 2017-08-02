package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

public class Server extends PyxServerAdapter {
    public Server(Config config) {
        super(config);
    }

    @Override
    @Nullable
    protected JsonObject onMessage(WebSocket conn, User user, JsonObject request, JsonObject response) throws GeneralException {
        return null;
    }
}
