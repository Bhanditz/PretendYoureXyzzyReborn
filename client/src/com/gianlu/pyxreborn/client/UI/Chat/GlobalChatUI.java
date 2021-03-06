package com.gianlu.pyxreborn.client.UI.Chat;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;


public class GlobalChatUI extends BaseChatController {
    public GlobalChatUI(Client client) {
        super(client);
    }

    public static void show(Client client) {
        UIClient.loadScene(null, "Global chat - Pretend You're Xyzzy Reborn", "Chat.fxml", new GlobalChatUI(client));
    }

    @Override
    protected boolean acceptEvent(Events event, JsonObject request) {
        return event == Events.CHAT;
    }

    @FXML
    @Override
    protected void send(MouseEvent event) {
        JsonObject req = client.createRequest(Operations.CHAT);
        req.addProperty(Fields.TEXT.toString(), message.getText());

        try {
            client.sendMessageBlocking(req);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        message.setText(null);
    }
}
