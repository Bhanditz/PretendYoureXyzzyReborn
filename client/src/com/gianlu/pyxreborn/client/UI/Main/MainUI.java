package com.gianlu.pyxreborn.client.UI.Main;

import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Client.CGame;
import com.gianlu.pyxreborn.Models.Client.CUser;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Chat.GameChatUI;
import com.gianlu.pyxreborn.client.UI.Game.GameUI;
import com.gianlu.pyxreborn.client.UI.ListCells.GameCell;
import com.gianlu.pyxreborn.client.UI.ListCells.UserCell;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainUI {
    private final Stage stage;
    private final Client client;
    private final CUser me;
    @FXML
    private ListView<CGame> gamesList;
    @FXML
    private ListView<CUser> usersList;

    public MainUI(Stage stage, Client client, CUser me) {
        this.stage = stage;
        this.client = client;
        this.me = me;
    }

    public static void show(Client client, CUser me) {
        Stage stage = new Stage();
        UIClient.loadScene(stage, me.nickname + " - Pretend You're Xyzzy Reborn", "Main.fxml", new MainUI(stage, client, me));
    }

    @FXML
    public void initialize() {
        gamesList.setCellFactory(param -> new GameCell(stage, client, me));
        usersList.setCellFactory(param -> new UserCell());
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
        ObservableList<CGame> games = new ObservableListWrapper<>(Utils.toList(gamesArray, CGame.class));
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
        ObservableList<CUser> users = new ObservableListWrapper<>(Utils.toList(usersArray, CUser.class));
        usersList.setItems(users);
    }

    @FXML
    public void createGame(MouseEvent mouseEvent) {
        CGame game;
        try {
            game = new CGame(client.sendMessageBlocking(client.createRequest(Operations.CREATE_GAME)).getAsJsonObject(Fields.GAME.toString()));
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        GameUI.show(stage, GameChatUI.show(client, game), client, me, game);
        stage.hide();

        refreshGamesList();
    }
}
