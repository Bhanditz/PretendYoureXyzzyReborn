package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PyxClientAdapter extends WebSocketClient {
    private final Map<Integer, IMessage> requests;
    private final JsonParser parser;
    private final Object waitingForResponse = new Object();
    private String sid;
    private PyxException blockingEx;
    private JsonObject blockingResp;

    /**
     * @param serverUri the server uri
     * @param nickname  the user's nickname
     * @param sid       if the user has disconnected recently, it can reconnect specifying the assigned SID and the same nickname
     * @param adminCode needed to login as an admin
     */
    public PyxClientAdapter(URI serverUri, String nickname, @Nullable String sid, @Nullable String adminCode) {
        super(serverUri, new CustomDraft(nickname, sid, adminCode));
        requests = new ConcurrentHashMap<>();
        parser = new JsonParser();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        sid = handshakedata.getFieldValue(Fields.SESSION_ID.toString());
        if (sid == null)
            throw new RuntimeException(new InvalidHandshakeException("The server handshake should contain a SID!"));

        Logger.info("Connection open! SID: " + sid);
    }

    public abstract void onEvent(Events event, JsonObject request);

    @Override
    public void onMessage(String message) {
        JsonObject obj = parser.parse(message).getAsJsonObject();
        if (obj.has(Fields.EVENT.toString())) {
            onEvent(Events.parse(obj.get(Fields.EVENT.toString()).getAsString()), obj);
            return;
        }

        JsonElement id = obj.get(Fields.ID.toString());
        if (id == null) return;

        IMessage listener = requests.remove(id.getAsInt());
        if (listener instanceof IMessageBlocking) {
            if (obj.has(Fields.ERROR_CODE.toString())) {
                blockingEx = new PyxException(obj);
                blockingResp = null;
                synchronized (waitingForResponse) {
                    waitingForResponse.notifyAll();
                }
            } else {
                blockingEx = null;
                blockingResp = obj;
                synchronized (waitingForResponse) {
                    waitingForResponse.notifyAll();
                }
            }
        } else {
            if (obj.has(Fields.ERROR_CODE.toString())) listener.onException(new PyxException(obj));
            else listener.onMessage(obj);
        }
    }

    public JsonObject createRequest(Operations op) {
        JsonObject req = new JsonObject();
        req.addProperty(Fields.SESSION_ID.toString(), sid);
        req.addProperty(Fields.OPERATION.toString(), op.toString());
        req.addProperty(Fields.ID.toString(), String.valueOf(new Random().nextInt()));
        return req;
    }

    public JsonObject sendMessageBlocking(JsonObject req) throws InterruptedException, PyxException {
        JsonElement id = req.get(Fields.ID.toString());
        if (id == null) throw new IllegalArgumentException("The request should contain an id!");

        requests.put(id.getAsInt(), new IMessageBlocking() {
            @Override
            public void onMessage(JsonObject resp) {

            }

            @Override
            public void onException(Exception ex) {

            }
        });
        send(req.toString());
        synchronized (waitingForResponse) {
            waitingForResponse.wait();
            if (blockingResp == null) throw blockingEx;
            return blockingResp;
        }
    }

    public void sendMessage(JsonObject req, IMessage listener) {
        JsonElement id = req.get(Fields.ID.toString());
        if (id == null) throw new IllegalArgumentException("The request should contain an id!");

        requests.put(id.getAsInt(), listener);
        send(req.toString());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Logger.info("Connection closed! #" + code + ": " + reason + " (remote=" + remote + ")");
    }

    @Override
    public void onError(Exception ex) {
        Logger.severe(ex);
    }

    public interface IMessage {
        void onMessage(JsonObject resp);

        void onException(Exception ex);
    }

    private interface IMessageBlocking extends IMessage {
    }

    private static class CustomDraft extends Draft_6455 {
        private final String nickname;
        private final String sid;
        private final String adminCode;

        public CustomDraft(String nickname, @Nullable String sid, String adminCode) {
            this.nickname = nickname;
            this.sid = sid;
            this.adminCode = adminCode;
        }

        @Override
        public Draft copyInstance() {
            return new CustomDraft(nickname, sid, adminCode);
        }

        @Override
        public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
            request.put(Fields.NICKNAME.toString(), nickname);
            if (sid != null) request.put(Fields.SESSION_ID.toString(), sid);
            if (adminCode != null) request.put(Fields.ADMIN_CODE.toString(), adminCode);
            return super.postProcessHandshakeRequestAsClient(request);
        }
    }
}
