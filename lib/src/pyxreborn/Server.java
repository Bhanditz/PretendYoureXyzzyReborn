package com.gianlu.pyxreborn;

import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class Server extends PyxServerAdapter {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());

    public Server(Config config) {
        super(config);
    }

    @Override
    @Nullable
    protected JsonElement onMessage(WebSocket conn, User user, JsonObject request) throws GeneralException {
        return null;
    }
}
