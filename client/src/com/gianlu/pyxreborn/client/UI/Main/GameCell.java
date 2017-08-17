package com.gianlu.pyxreborn.client.UI.Main;

import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Chat.GameChat;
import com.gianlu.pyxreborn.client.UI.Game;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GameCell extends ListCell<JsonObject> {
    private final Stage mainStage;
    private final Client client;

    public GameCell(Stage mainStage, Client client) {
        this.mainStage = mainStage;
        this.client = client;
    }

    @Override
    protected void updateItem(JsonObject item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameCell.fxml"));
            loader.setController(new Controller(item));

            try {
                setGraphic(loader.load());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class Controller {
        private final JsonObject item;
        private final String gameName;
        private final int playersNum;
        private final int spectatorsNum;
        @FXML
        private Label name;
        @FXML
        private Label players;
        @FXML
        private Label spectators;

        public Controller(JsonObject item) {
            this.item = item;
            this.gameName = item.get(Fields.HOST.toString()).getAsJsonObject().get(Fields.NICKNAME.toString()).getAsString();
            this.playersNum = item.getAsJsonArray(Fields.PLAYERS.toString()).size();
            this.spectatorsNum = item.getAsJsonArray(Fields.SPECTATORS.toString()).size();
        }

        @FXML
        public void join(MouseEvent event) {
            int gid = item.get(Fields.GID.toString()).getAsInt();
            JsonObject req = client.createRequest(Operations.JOIN_GAME);
            req.addProperty(Fields.GID.toString(), gid);
            try {
                client.sendMessageBlocking(req);
            } catch (InterruptedException | PyxException ex) {
                UIClient.notifyException(ex);
                return;
            }

            GameChat.show(client, gameName, gid);
            Game.show(client, gameName);
            mainStage.close();
        }

        @FXML
        public void spectate(MouseEvent event) {
            throw new UnsupportedOperationException("Not implemented yet!"); // TODO
        }

        @FXML
        public void initialize() {
            name.setText(gameName);
            players.setText("Players: " + playersNum);
            spectators.setText("Spectators: " + spectatorsNum);
        }
    }
}
