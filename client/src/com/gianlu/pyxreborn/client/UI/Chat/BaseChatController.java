package com.gianlu.pyxreborn.client.UI.Chat;

import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.client.Client;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public abstract class BaseChatController {
    protected final Client client;
    @FXML
    protected TextField message;
    @FXML
    protected ListView<String> chat;

    public BaseChatController(Client client) {
        this.client = client;
    }

    @FXML
    public final void initialize() {
        ObservableList<String> items = new ObservableListWrapper<>(new ArrayList<>());
        chat.setItems(items);
        client.addListener(this::acceptEvent, (event, obj) -> {
            String msg = obj.get(Fields.NICKNAME.toString()).getAsString() + ": " + obj.get(Fields.TEXT.toString()).getAsString();
            Platform.runLater(() -> chat.getItems().add(msg));
        });
    }

    protected abstract boolean acceptEvent(Events event, JsonObject request);

    protected final void notifyException(Throwable ex) {
        new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
    }

    @FXML
    protected abstract void send(MouseEvent event);
}
