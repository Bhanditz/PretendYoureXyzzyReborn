package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.logging.Logger;

public abstract class PyxServerAdapter extends WebSocketServer {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final static int CLOSE = 1001;
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
                users.checkAndAdd(handshake.getFieldValue(Fields.NICKNAME.toString()), conn.getRemoteSocketAddress());
            } catch (GeneralException ex) {
                conn.close(CLOSE, ex.code.toString());
            }
        } else {
            conn.close(CLOSE, ErrorCodes.INVALID_REQUEST.toString());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) { // TODO
        if (remote) LOGGER.info("Client closed connection: " + reason);
        else LOGGER.info("Client disconnect from server: " + reason);
    }

    @Nullable
    protected abstract JsonObject onMessage(WebSocket conn, User user, JsonObject request, JsonObject response) throws GeneralException;

    @Override
    public final void onMessage(WebSocket conn, String message) {
        JsonObject req = new JsonParser().parse(message).getAsJsonObject();
        if (req.has(Fields.SESSION_ID.toString())) {
            User user = users.findBySessionId(req.get(Fields.SESSION_ID.toString()).getAsString());
            if (user == null) {
                sendErrorCode(conn, ErrorCodes.NOT_CONNECTED);
            } else {
                try {
                    JsonElement resp = onMessage(conn, user, req, createResponse(req.get(Fields.ID.toString())));
                    if (resp != null) sendMessage(user, resp);
                } catch (GeneralException ex) {
                    sendErrorCode(conn, ex.code);
                }
            }
        } else {
            sendErrorCode(conn, ErrorCodes.INVALID_REQUEST);
        }
    }

    private JsonObject createResponse(@Nullable JsonElement id) {
        JsonObject resp = new JsonObject();
        if (id != null) resp.addProperty(Fields.ID.toString(), id.getAsString());
        return resp;
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.severe(ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        LOGGER.info("Server started on port " + getPort());
    }

    private void sendErrorCode(WebSocket conn, ErrorCodes code) {
        LOGGER.info("Error code sent to " + conn.getRemoteSocketAddress() + ": " + code.toString());
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.ERROR_CODE.toString(), code.toString());
        sendMessage(conn, obj);
    }

    public void broadcastMessage(JsonElement message) {
        for (User user : users) sendMessage(user, message);
    }

    public void sendMessage(WebSocket socket, JsonElement message) {
        socket.send(message.toString());
    }

    public void sendMessage(User user, JsonElement message) {
        for (WebSocket client : connections())
            if (Objects.equals(client.getRemoteSocketAddress(), user.address))
                sendMessage(client, message);
    }
}
