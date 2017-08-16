package com.gianlu.pyxreborn.client.UI;

import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Chat.GlobalChat;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;

public class Register {
    private final Stage stage;
    @FXML
    private TextField nickname;
    @FXML
    private TextField address;

    public Register(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void register() {
        String nickname = this.nickname.getText();

        try {
            Client client = new Client(new URI(address.getText()), nickname, null);
            if (client.connectBlocking()) {
                stage.close();
                UIClient.loadScene(null, nickname + " - Pretend You're Xyzzy Reborn", "Main.fxml", new Main(client));
                GlobalChat.show(client);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed connecting!").show();
            }
        } catch (InterruptedException | URISyntaxException ex) {
            UIClient.notifyException(ex);
        }
    }
}
