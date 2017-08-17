package com.gianlu.pyxreborn.client.UI;

import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Chat.GlobalChat;
import com.gianlu.pyxreborn.client.UI.Main.Main;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URISyntaxException;

public class Register {
    private final Stage stage;
    @FXML
    private TextField nickname;
    @FXML
    private TextField address;
    @FXML
    private TextField adminCode;
    @FXML
    private CheckBox admin;

    public Register(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void adminCheckedChanged(MouseEvent event) {
        if (admin.isSelected()) adminCode.setOpacity(1);
        else adminCode.setOpacity(0);
    }

    @FXML
    public void register() {
        String nickname = this.nickname.getText();

        try {
            Client client = new Client(new URI(address.getText()), nickname, null, admin.isSelected() ? adminCode.getText() : null);
            if (client.connectBlocking()) {
                JsonObject me;
                try {
                    me = client.sendMessageBlocking(client.createRequest(Operations.GET_ME)).getAsJsonObject(Fields.USER.toString());
                } catch (PyxException ex) {
                    UIClient.notifyException(ex);
                    return;
                }

                stage.close();
                GlobalChat.show(client);
                Main.show(client, me, nickname);
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed connecting!").show();
            }
        } catch (InterruptedException | URISyntaxException ex) {
            UIClient.notifyException(ex);
        }
    }
}
