package com.gianlu.pyxreborn.client.UI;

import com.gianlu.pyxreborn.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;

public class Register {
    public TextField nickname;

    @FXML
    public void register() {
        String nickname = this.nickname.getText();

        Client client = new Client(URI.create("ws://localhost:6969"), nickname, null);
        try {
            if (client.connectBlocking()) {
                Stage stage = StartupUI.getStage();
                Main main = StartupUI.loadScene(stage, nickname + " - Pretend You're Xyzzy Reborn", "Main.fxml");
                main.setClient(client);
                main.refreshEverything();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed connecting!").show();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
