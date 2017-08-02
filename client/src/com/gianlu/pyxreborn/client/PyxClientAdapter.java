package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.exceptions.InvalidHandshakeException;
import org.java_websocket.handshake.ClientHandshakeBuilder;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Random;

public abstract class PyxClientAdapter extends WebSocketClient {
    private String sid;

    public PyxClientAdapter(URI serverUri, String nickname) {
        super(serverUri, new NicknameDraft(nickname));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        sid = handshakedata.getFieldValue(Fields.SESSION_ID.toString());
        if (sid == null)
            throw new RuntimeException(new InvalidHandshakeException("The server handshake should contain a SID!"));

        System.out.println("OPEN! SID: " + sid);
    }

    public JsonObject createRequest(Operations op) {
        JsonObject req = new JsonObject();
        req.addProperty(Fields.SESSION_ID.toString(), sid);
        req.addProperty(Fields.OPERATION.toString(), op.toString());
        req.addProperty(Fields.ID.toString(), String.valueOf(new Random().nextInt()));
        return req;
    }

    public void sendMessage(JsonObject req) {
        send(req.toString());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("MSG: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("CLOSED: #" + code + ": " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
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
