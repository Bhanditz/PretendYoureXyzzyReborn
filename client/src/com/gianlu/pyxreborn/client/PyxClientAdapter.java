package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Utils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public abstract class PyxClientAdapter extends WebSocketClient {
    public PyxClientAdapter(URI serverUri, String nickname) {
        super(serverUri, new Draft_6455(), Utils.singletonMap(Fields.NICKNAME.toString(), nickname), 0);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("OPEN!");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("MSG: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("CLOSED: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
