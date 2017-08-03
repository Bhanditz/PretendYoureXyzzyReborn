package com.gianlu.pyxreborn.client;

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

import java.net.URI;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

// TODO: Should implement #sendMessageBlocking but I have no idea on how to do it
public abstract class PyxClientAdapter extends WebSocketClient {
    private final Map<Integer, IMessage> requests;
    private final JsonParser parser;
    private String sid;

    public PyxClientAdapter(URI serverUri, String nickname) {
        super(serverUri, new NicknameDraft(nickname));
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

    @Override
    public void onMessage(String message) {
        JsonObject obj = parser.parse(message).getAsJsonObject();
        JsonElement id = obj.get(Fields.ID.toString());
        if (id == null) return;

        IMessage listener = requests.remove(id.getAsInt());
        if (obj.has(Fields.ERROR_CODE.toString())) listener.onException(new PyxException(obj));
        else listener.onMessage(obj);

        Logger.info("Message received: " + message);
    }

    public JsonObject createRequest(Operations op) {
        JsonObject req = new JsonObject();
        req.addProperty(Fields.SESSION_ID.toString(), sid);
        req.addProperty(Fields.OPERATION.toString(), op.toString());
        req.addProperty(Fields.ID.toString(), String.valueOf(new Random().nextInt()));
        return req;
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

    private static class NicknameDraft extends Draft_6455 {
        private final String nickname;

        public NicknameDraft(String nickname) {
            this.nickname = nickname;
        }

        @Override
        public Draft copyInstance() {
            return new NicknameDraft(nickname);
        }

        @Override
        public ClientHandshakeBuilder postProcessHandshakeRequestAsClient(ClientHandshakeBuilder request) {
            request.put(Fields.NICKNAME.toString(), nickname);
            return super.postProcessHandshakeRequestAsClient(request);
        }
    }
}
