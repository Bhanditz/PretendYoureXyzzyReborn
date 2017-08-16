package com.gianlu.pyxreborn.client.UI.Chat;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.StartupUI;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;


public class GlobalChat extends BaseChatController {
    public GlobalChat(Client client) {
        super(client);
    }

    public static void show(Client client) {
        StartupUI.loadScene(null, "Global chat - Pretend You're Xyzzy Reborn", "Chat.fxml", new GlobalChat(client));
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
            notifyException(ex);
            return;
        }

        message.setText(null);
    }
}
