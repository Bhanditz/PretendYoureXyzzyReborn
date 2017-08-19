package com.gianlu.pyxreborn.client.UI.ListCells;

import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Client.CGame;
import com.gianlu.pyxreborn.Models.Client.CUser;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Chat.GameChatUI;
import com.gianlu.pyxreborn.client.UI.Game.GameUI;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GameCell extends ListCell<CGame> {
    private final Stage mainStage;
    private final Client client;
    private final CUser me;

    public GameCell(Stage mainStage, Client client, CUser me) {
        this.mainStage = mainStage;
        this.client = client;
        this.me = me;
    }

    @Override
    protected void updateItem(CGame item, boolean empty) {
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
        private final CGame item;
        @FXML
        private Label name;
        @FXML
        private Label players;
        @FXML
        private Label spectators;

        public Controller(CGame item) {
            this.item = item;
        }

        @FXML
        public void join(MouseEvent event) {
            JsonObject req = client.createRequest(Operations.JOIN_GAME);
            req.addProperty(Fields.GID.toString(), item.gid);
            try {
                client.sendMessageBlocking(req);
            } catch (InterruptedException | PyxException ex) {
                UIClient.notifyException(ex);
                return;
            }

            GameUI.show(mainStage, GameChatUI.show(client, item), client, me, item);
            mainStage.hide();
        }

        @FXML
        public void spectate(MouseEvent event) {
            JsonObject req = client.createRequest(Operations.SPECTATE_GAME);
            req.addProperty(Fields.GID.toString(), item.gid);
            try {
                client.sendMessageBlocking(req);
            } catch (InterruptedException | PyxException ex) {
                UIClient.notifyException(ex);
                return;
            }

            GameUI.show(mainStage, GameChatUI.show(client, item), client, me, item);
            mainStage.hide();
        }

        @FXML
        public void initialize() {
            name.setText(item.host.nickname);
            players.setText("Players: " + item.players.size());
            spectators.setText("Spectators: " + item.spectators.size());
        }
    }
}
