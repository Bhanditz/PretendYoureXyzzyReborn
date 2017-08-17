package com.gianlu.pyxreborn.server;

import com.gianlu.pyxreborn.Exceptions.ErrorCodes;
import com.gianlu.pyxreborn.Exceptions.GeneralException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Models.Player;
import com.gianlu.pyxreborn.Models.User;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.server.Lists.CardSets;
import com.gianlu.pyxreborn.server.Lists.ConnectedUsers;
import com.gianlu.pyxreborn.server.Lists.Games;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public abstract class PyxServerAdapter extends WebSocketServer {
    private final static Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final static int ADMIN_CODE_REFRESH_RATE = 60; // sec
    public final ConnectedUsers users;
    public final Games games;
    public final Config config;
    public final CardSets cardSets;
    private final JsonParser parser;
    private String currentAdminCode;

    public PyxServerAdapter(Config config, CardSets cardSets) {
        super(new InetSocketAddress(config.serverPort));
        this.config = config;
        this.users = new ConnectedUsers(this);
        this.cardSets = cardSets;
        this.games = new Games(this);
        this.parser = new JsonParser();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshAdminCode();
            }
        }, 0, ADMIN_CODE_REFRESH_RATE * 1000);
    }

    private void refreshAdminCode() {
        currentAdminCode = Utils.generateAlphanumericString(24);
        LOGGER.info("Current admin code: " + currentAdminCode);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info("Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        ServerHandshakeBuilder builder = super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
        if (request.hasFieldValue(Fields.NICKNAME.toString())) {
            boolean admin = false;
            if (request.hasFieldValue(Fields.ADMIN_CODE.toString())) {
                String code = request.getFieldValue(Fields.ADMIN_CODE.toString());
                if (Objects.equals(code, currentAdminCode)) {
                    admin = true;
                } else {
                    throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, ErrorCodes.INVALID_ADMIN_CODE.toString());
                }
            }

            try {
                User user = users.checkAndAdd(request.getFieldValue(Fields.NICKNAME.toString()), request.getFieldValue(Fields.SESSION_ID.toString()), conn.getRemoteSocketAddress(), admin);
                builder.put(Fields.SESSION_ID.toString(), user.sessionId);
            } catch (GeneralException ex) {
                throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, ex.code.toString());
            }
        } else {
            throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, ErrorCodes.INVALID_REQUEST.toString());
        }

        // FIXME: Not working, but I've opened an issue: https://github.com/TooTallNate/Java-WebSocket/issues/530

        return builder;
    }

    @Nullable
    protected abstract JsonObject onMessage(WebSocket conn, User user, JsonObject request, JsonObject response) throws GeneralException;

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if (remote) LOGGER.info("Client closed connection: " + reason);
        else LOGGER.info("Client disconnect from server: " + reason);

        users.removeUser(conn.getRemoteSocketAddress(), code == 1006);
    }

    @Override
    public final void onMessage(WebSocket conn, String message) {
        JsonObject req = parser.parse(message).getAsJsonObject();
        if (req.has(Fields.SESSION_ID.toString()) && req.has(Fields.OPERATION.toString())) {
            User user = users.findBySessionId(req.get(Fields.SESSION_ID.toString()).getAsString());
            if (user == null) {
                sendErrorCode(conn, req.get(Fields.ID.toString()), ErrorCodes.NOT_CONNECTED);
            } else {
                try {
                    JsonElement resp = onMessage(conn, user, req, createResponse(req.get(Fields.ID.toString())));
                    if (resp != null) sendMessage(user, resp);
                } catch (GeneralException ex) {
                    sendErrorCode(conn, req.get(Fields.ID.toString()), ex.code);
                    if (ex.getCause() != null) ex.getCause().printStackTrace();
                }
            }
        } else {
            sendErrorCode(conn, req.get(Fields.ID.toString()), ErrorCodes.INVALID_REQUEST);
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

    @Nullable
    public WebSocket findWebSocketByAddress(InetSocketAddress address) {
        for (WebSocket socket : connections())
            if (Objects.equals(socket.getRemoteSocketAddress(), address))
                return socket;

        return null;
    }

    @Override
    public void onStart() {
        LOGGER.info("Server started on port " + getPort());
    }

    private void sendErrorCode(WebSocket conn, JsonElement id, ErrorCodes code) {
        LOGGER.info("Error code sent to " + conn.getRemoteSocketAddress() + ": " + code.toString());
        JsonObject obj = createResponse(id);
        obj.addProperty(Fields.ERROR_CODE.toString(), code.toString());
        sendMessage(conn, obj);
    }

    public void broadcastMessage(JsonElement message) {
        for (User user : users) sendMessage(user, message);
    }

    public void broadcastMessageToPlayers(Game game, JsonElement message) {
        for (Player player : game.players)
            sendMessage(player.user, message);
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
