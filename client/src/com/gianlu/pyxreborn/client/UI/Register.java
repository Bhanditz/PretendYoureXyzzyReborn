package com.gianlu.pyxreborn.client.UI;

import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Chat.GlobalChat;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;

public class Register {
    private final Stage stage;
    @FXML
    private TextField nickname;

    public Register(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void register() {
        String nickname = this.nickname.getText();

        Client client = new Client(URI.create("ws://localhost:6969"), nickname, null);
        try {
            if (client.connectBlocking()) {
                stage.close();
                UIClient.loadScene(null, nickname + " - Pretend You're Xyzzy Reborn", "Main.fxml", new Main(client));
                GlobalChat.show(client);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed connecting!").show();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
