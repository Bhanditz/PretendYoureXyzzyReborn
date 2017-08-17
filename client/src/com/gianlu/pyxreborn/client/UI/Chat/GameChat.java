package com.gianlu.pyxreborn.client.UI.Chat;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GameChat extends BaseChatController {
    private final int gameId;

    public GameChat(Client client, int gameId) {
        super(client);
        this.gameId = gameId;
    }

    public static Stage show(Client client, String gameName, int gameId) {
        Stage stage = new Stage();
        UIClient.loadScene(stage, gameName + " chat - Pretend You're Xyzzy Reborn", "Chat.fxml", new GameChat(client, gameId));
        return stage;
    }

    @Override
    protected boolean acceptEvent(Events event, JsonObject request) {
        if (event == Events.GAME_CHAT) {
            JsonElement gid = request.get(Fields.GID.toString());
            if (gid != null && gid.getAsInt() == gameId) return true;
        }

        return false;
    }

    @Override
    protected void send(MouseEvent event) {
        JsonObject req = client.createRequest(Operations.GAME_CHAT);
        req.addProperty(Fields.TEXT.toString(), message.getText());
        req.addProperty(Fields.GID.toString(), gameId);

        try {
            client.sendMessageBlocking(req);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        message.setText(null);
    }
}
