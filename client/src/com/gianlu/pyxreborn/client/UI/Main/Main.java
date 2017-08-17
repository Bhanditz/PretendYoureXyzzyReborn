package com.gianlu.pyxreborn.client.UI.Main;

import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Chat.GameChat;
import com.gianlu.pyxreborn.client.UI.Game;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main {
    private final Stage stage;
    private final Client client;
    @FXML
    private ListView<JsonObject> gamesList;
    @FXML
    private ListView<String> usersList;

    public Main(Stage stage, Client client) {
        this.stage = stage;
        this.client = client;
    }

    public static void show(Client client, String nickname) {
        Stage stage = new Stage();
        UIClient.loadScene(stage, nickname + " - Pretend You're Xyzzy Reborn", "Main.fxml", new Main(stage, client));
    }

    @FXML
    public void initialize() {
        gamesList.setCellFactory(param -> new GameCell(stage, client));
        refreshGamesList();
        refreshUsersList();
    }

    @FXML
    public void refreshGamesList() {
        JsonObject resp;
        try {
            resp = client.sendMessageBlocking(client.createRequest(Operations.LIST_GAMES));
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        JsonArray gamesArray = resp.getAsJsonArray(Fields.GAMES_LIST.toString());
        ObservableList<JsonObject> games = new ObservableListWrapper<>(new ArrayList<JsonObject>());
        for (JsonElement element : gamesArray) games.add(element.getAsJsonObject());
        gamesList.setItems(games);
    }

    @FXML
    public void refreshUsersList() {
        JsonObject resp;
        try {
            resp = client.sendMessageBlocking(client.createRequest(Operations.LIST_USERS));
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
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

    @FXML
    public void createGame(MouseEvent mouseEvent) {
        JsonObject resp;
        try {
            resp = client.sendMessageBlocking(client.createRequest(Operations.CREATE_GAME));
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        String gameName = resp.get(Fields.HOST.toString()).getAsJsonObject().get(Fields.NICKNAME.toString()).getAsString();
        int gid = resp.get(Fields.GID.toString()).getAsInt();

        new Alert(Alert.AlertType.INFORMATION, "New game ID: " + gid).show();
        GameChat.show(client, gameName, gid);
        Game.show(client, gameName);
        stage.close();
    }
}