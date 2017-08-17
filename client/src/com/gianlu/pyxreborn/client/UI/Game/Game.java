package com.gianlu.pyxreborn.client.UI.Game;


import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Card.PyxCard;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class Game implements Client.IEventListener {
    private final Stage mainStage;
    private final Stage chatStage;
    private final Stage stage;
    private final Client client;
    private final JsonObject me;
    private final int gameId;
    private final ObservableList<JsonObject> playersList = new ObservableListWrapper<>(new ArrayList<>());
    private final ObservableList<JsonObject> spectatorsList = new ObservableListWrapper<>(new ArrayList<>());
    private JsonObject game;
    @FXML
    private Button startGame;
    @FXML
    private Pane blackCard;
    @FXML
    private ScrollPane playedCards;
    @FXML
    private ListView<JsonObject> players;
    @FXML
    private ListView<JsonObject> spectators;
    @FXML
    private Label instructions;
    @FXML
    private ScrollPane hand;
    private JsonObject judge;

    public Game(Stage mainStage, Stage chatStage, Stage stage, Client client, JsonObject me, int gameId) {
        this.mainStage = mainStage;
        this.chatStage = chatStage;
        this.stage = stage;
        this.client = client;
        this.me = me;
        this.gameId = gameId;
    }

    public static void show(Stage mainStage, Stage chatStage, Client client, JsonObject me, String gameName, int gameId) {
        Stage stage = new Stage();
        UIClient.loadScene(stage, gameName + " game - Pretend You're Xyzzy Reborn", "Game.fxml", new Game(mainStage, chatStage, stage, client, me, gameId));
    }

    @FXML
    public void initialize() {
        JsonObject obj = client.createRequest(Operations.GET_GAME);
        obj.addProperty(Fields.GID.toString(), gameId);
        try {
            game = client.sendMessageBlocking(obj).getAsJsonObject(Fields.GAME.toString());
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        if (Objects.equals(me.get(Fields.NICKNAME.toString()).getAsString(), game.getAsJsonObject(Fields.HOST.toString()).get(Fields.NICKNAME.toString()).getAsString())) {  // I am the host
            if (com.gianlu.pyxreborn.Models.Game.Status.parse(game.get(Fields.STATUS.toString()).getAsString()) == com.gianlu.pyxreborn.Models.Game.Status.LOBBY) {
                startGame.setVisible(true);
            } else {
                startGame.setVisible(false);
            }
        } else { // I am not the host
            startGame.setVisible(false);
        }

        players.setCellFactory(param -> new PlayerCell());
        spectators.setCellFactory(param -> new SpectatorCell());
        refreshPlayersAndSpectators();

        client.addListener((event1, request) -> true, this);
    }

    private void refreshPlayersAndSpectators() {
        playersList.clear();
        playersList.addAll(Utils.toJsonObjectsList(game.getAsJsonArray(Fields.PLAYERS.toString())));
        players.setItems(playersList);

        spectatorsList.clear();
        spectatorsList.addAll(Utils.toJsonObjectsList(game.getAsJsonArray(Fields.SPECTATORS.toString())));
        spectators.setItems(spectatorsList);
    }

    @FXML
    public void startGame(MouseEvent event) {
        JsonObject obj = client.createRequest(Operations.START_GAME);
        obj.addProperty(Fields.GID.toString(), game.get(Fields.GID.toString()).getAsInt());
        try {
            client.sendMessageBlocking(obj);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
        }
    }

    @FXML
    public void leaveGame(MouseEvent event) {
        JsonObject obj = client.createRequest(Operations.LEAVE_GAME);
        obj.addProperty(Fields.GID.toString(), game.get(Fields.GID.toString()).getAsInt());
        try {
            client.sendMessageBlocking(obj);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
        }

        stage.close();
        chatStage.close();
        mainStage.show();
    }

    private void refreshGame() {
        JsonObject obj = client.createRequest(Operations.GET_GAME);
        obj.addProperty(Fields.GID.toString(), gameId);
        try {
            game = client.sendMessageBlocking(obj).getAsJsonObject(Fields.GAME.toString());
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
        }
    }

    @Override
    public void onMessage(Events event, JsonObject obj) {
        System.out.println("EVENT MOTHERFUCKER: " + obj);

        switch (event) {
            case GAME_CHAT:
            case NEW_GAME:
            case NEW_USER:
            case GAME_REMOVED:
            case USER_LEFT:
            case CHAT:
                // Not interested
                return;
            case GAME_NEW_PLAYER:
            case GAME_PLAYER_LEFT:
                refreshGame();
                refreshPlayersAndSpectators();
                break;
            case GAME_HAND_CHANGED:
                // TODO: GAME_HAND_CHANGED (ghc): {"ev":"ghc","H":[{"id":369,"t":"Booby-trapping the house to foil burglars.","w":null},{"id":346,"t":"Shaquille O'Neal's acting career.","w":null},{"id":451,"t":"Peeing a little bit.","w":null},{"id":320,"t":"Cockfights.","w":null},{"id":392,"t":"White privilege.","w":null},{"id":340,"t":"Being on fire.","w":null},{"id":132,"t":"Opposable thumbs.","w":null},{"id":204,"t":"A murder most foul.","w":null},{"id":242,"t":"Friendly fire.","w":null},{"id":353,"t":"Ring Pops&trade;.","w":null}]}
                break;
            case GAME_NEW_ROUND:
                judge = obj.getAsJsonObject(Fields.JUDGE.toString());
                JsonObject blackCard = obj.getAsJsonObject(Fields.BLACK_CARD.toString());
                Platform.runLater(() -> {
                    Game.this.blackCard.getChildren().clear();
                    Game.this.blackCard.getChildren().add(new PyxCard(blackCard));
                });
                break;
            case GAME_JUDGING:
                break;
            case GAME_ROUND_ENDED:
                break;
        }
    }
}
