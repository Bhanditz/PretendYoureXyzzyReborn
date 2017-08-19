package com.gianlu.pyxreborn;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public abstract class PyxServerAdapter extends WebSocketServer {
    private final Config config;
    private final ConnectedUsers users;

    public PyxServerAdapter(Config config) {
        super(new InetSocketAddress(config.serverPort));
        this.config = config;
        this.users = new ConnectedUsers(this, config.maxUsers);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if (handshake.hasFieldValue(Fields.NICKNAME.toString())) {
            try {
                users.checkAndAdd(handshake.getFieldValue(Fields.NICKNAME.toString()));
            } catch (GeneralException ex) {
                conn.closeConnection(1, ex.code.toString());
            }
        } else {
            conn.closeConnection(1, ErrorCodes.INVALID_REQUEST.toString());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Nullable
    protected abstract JsonElement onMessage(WebSocket conn, User user, JsonObject request) throws GeneralException;

    @Override
    public final void onMessage(WebSocket conn, String message) {
        JsonObject obj = new JsonParser().parse(message).getAsJsonObject();
        if (obj.has(Fields.SESSION_ID.toString())) {
            User user = users.findBySessionId(obj.get(Fields.SESSION_ID.toString()).getAsString());
            if (user == null) {
                sendErrorCode(conn, ErrorCodes.NOT_CONNECTED);
            } else {
                try {
                    JsonElement resp = onMessage(conn, user, obj);
                    if (resp != null) sendMessage(user, resp);
                } catch (GeneralException ex) {
                    sendErrorCode(conn, ex.code);
                }
            }
        } else {
            sendErrorCode(conn, ErrorCodes.INVALID_REQUEST);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Override
    public void onStart() {

    }

    private void sendErrorCode(WebSocket conn, ErrorCodes code) {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.ERROR_CODE.toString(), code.toString());
        sendMessage(conn, obj);
    }

    public void broadcastMessage(JsonElement message) {
        for (User user : users) sendMessage(user, message);
    }

    public void sendMessage(WebSocket socket, JsonElement message) { // TODO

    }

    public void sendMessage(User user, JsonElement message) { // TODO

    }
}
