package com.gianlu.pyxreborn.client.UI;

import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class Main {
    public ListView<String> gamesList;
    public ListView<String> usersList;
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    public void refreshEverything() {
        refreshGamesList();
        refreshUsersList();
    }

    @FXML
    public void refreshGamesList() {
        JsonObject resp;
        try {
            resp = client.sendMessageBlocking(client.createRequest(Operations.LIST_GAMES));
        } catch (InterruptedException | PyxException ex) {
            notifyException(ex);
            return;
        }

        JsonArray gamesArray = resp.getAsJsonArray(Fields.GAMES_LIST.toString());
        ObservableList<String> games = new ObservableListWrapper<>(new ArrayList<String>());

        for (JsonElement element : gamesArray) {
            JsonObject game = element.getAsJsonObject();
            String host = game.get(Fields.HOST.toString()).getAsJsonObject().get(Fields.NICKNAME.toString()).getAsString();
            int gid = game.get(Fields.GID.toString()).getAsInt();
            games.add(host + " (" + gid + ")");
        }

        gamesList.setItems(games);
    }

    @FXML
    public void refreshUsersList() {
        JsonObject resp;
        try {
            resp = client.sendMessageBlocking(client.createRequest(Operations.LIST_USERS));
        } catch (InterruptedException | PyxException ex) {
            notifyException(ex);
            return;
        }

        JsonArray usersArray = resp.getAsJsonArray(Fields.USERS_LIST.toString());
        ObservableList<String> users = new ObservableListWrapper<>(new ArrayList<String>());

        for (JsonElement element : usersArray) {
            JsonObject user = element.getAsJsonObject();
            users.add(user.get(Fields.NICKNAME.toString()).getAsString());
        }

        usersList.setItems(users);
    }

    private void notifyException(Throwable ex) {
        new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
    }

    @FXML
    public void createGame(MouseEvent mouseEvent) {
        JsonObject resp;
        try {
            resp = client.sendMessageBlocking(client.createRequest(Operations.CREATE_GAME));
        } catch (InterruptedException | PyxException ex) {
            notifyException(ex);
            return;
        }

        new Alert(Alert.AlertType.INFORMATION, "New game ID: " + resp.get(Fields.GID.toString()).getAsInt()).show();
        refreshGamesList();
    }
}
