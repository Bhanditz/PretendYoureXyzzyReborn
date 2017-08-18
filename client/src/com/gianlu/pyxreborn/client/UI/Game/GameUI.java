package com.gianlu.pyxreborn.client.UI.Game;


import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.BlackCard;
import com.gianlu.pyxreborn.Models.Client.CGame;
import com.gianlu.pyxreborn.Models.Client.CPlayer;
import com.gianlu.pyxreborn.Models.Client.CUser;
import com.gianlu.pyxreborn.Models.Game;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Card.PyxCard;
import com.gianlu.pyxreborn.client.UI.ListCells.PlayerCell;
import com.gianlu.pyxreborn.client.UI.ListCells.UserCell;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class GameUI implements Client.IEventListener {
    private final Stage mainStage;
    private final Stage chatStage;
    private final Stage stage;
    private final Client client;
    private final CUser me;
    private final ObservableList<CPlayer> playersList = new ObservableListWrapper<>(new ArrayList<>());
    private final ObservableList<CUser> spectatorsList = new ObservableListWrapper<>(new ArrayList<>());
    private CGame game;
    private CPlayer judge;
    @FXML
    private Button startGame;
    @FXML
    private Pane blackCard;
    @FXML
    private HBox playedCards;
    @FXML
    private ListView<CPlayer> players;
    @FXML
    private ListView<CUser> spectators;
    @FXML
    private Label instructions;
    @FXML
    private HBox hand;

    public GameUI(Stage mainStage, Stage chatStage, Stage stage, Client client, CUser me, CGame game) {
        this.mainStage = mainStage;
        this.chatStage = chatStage;
        this.stage = stage;
        this.client = client;
        this.me = me;
        this.game = game;
    }

    public static void show(Stage mainStage, Stage chatStage, Client client, CUser me, CGame game) {
        Stage stage = new Stage();
        UIClient.loadScene(stage, game.host.nickname + " game - Pretend You're Xyzzy Reborn", "Game.fxml", new GameUI(mainStage, chatStage, stage, client, me, game));
    }

    @FXML
    public void initialize() {
        JsonObject obj = client.createRequest(Operations.GET_GAME);
        obj.addProperty(Fields.GID.toString(), game.gid);
        try {
            game = new CGame(client.sendMessageBlocking(obj).getAsJsonObject(Fields.GAME.toString()));
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        if (Objects.equals(me.nickname, game.host.nickname)) {  // I am the host
            if (game.status == Game.Status.LOBBY) startGame.setVisible(true);
            else startGame.setVisible(false);
        } else { // I am not the host
            startGame.setVisible(false);
        }

        spectators.setCellFactory(param -> new UserCell());
        spectatorsList.addAll(game.spectators);
        spectators.setItems(spectatorsList);

        players.setCellFactory(param -> new PlayerCell());
        playersList.addAll(game.players);
        players.setItems(playersList);

        client.addListener((event1, request) -> true, this);
    }

    @FXML
    public void startGame(MouseEvent event) {
        JsonObject obj = client.createRequest(Operations.START_GAME);
        obj.addProperty(Fields.GID.toString(), game.gid);
        try {
            client.sendMessageBlocking(obj);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
        }
    }

    @FXML
    public void leaveGame(MouseEvent event) {
        JsonObject obj = client.createRequest(Operations.LEAVE_GAME);
        obj.addProperty(Fields.GID.toString(), game.gid);
        try {
            client.sendMessageBlocking(obj);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
        }

        stage.close();
        chatStage.close();
        mainStage.show();
    }

    @Override
    public void onMessage(Events event, JsonObject obj) {
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
                Platform.runLater(() -> playersList.add(new CPlayer(obj.getAsJsonObject(Fields.PLAYER.toString()))));
                break;
            case GAME_PLAYER_LEFT:
                Platform.runLater(() -> {
                    CPlayer toRemove = Utils.find(playersList, obj.get(Fields.NICKNAME.toString()).getAsString());
                    if (toRemove != null) playersList.remove(toRemove);
                });
                break;
            case GAME_HAND_CHANGED:
                // TODO: GAME_HAND_CHANGED (ghc): {"ev":"ghc","H":[{"id":369,"t":"Booby-trapping the house to foil burglars.","w":null},{"id":346,"t":"Shaquille O'Neal's acting career.","w":null},{"id":451,"t":"Peeing a little bit.","w":null},{"id":320,"t":"Cockfights.","w":null},{"id":392,"t":"White privilege.","w":null},{"id":340,"t":"Being on fire.","w":null},{"id":132,"t":"Opposable thumbs.","w":null},{"id":204,"t":"A murder most foul.","w":null},{"id":242,"t":"Friendly fire.","w":null},{"id":353,"t":"Ring Pops&trade;.","w":null}]}
                break;
            case GAME_NEW_ROUND:
                judge = new CPlayer(obj.getAsJsonObject(Fields.JUDGE.toString()));
                BlackCard blackCard = new BlackCard(obj.getAsJsonObject(Fields.BLACK_CARD.toString()));
                Platform.runLater(() -> {
                    GameUI.this.blackCard.getChildren().clear();
                    GameUI.this.blackCard.getChildren().add(new PyxCard(blackCard));
                });
                break;
            case GAME_JUDGING:
                break;
            case GAME_ROUND_ENDED:
                break;
            case GAME_STOPPED:
                break;
        }
    }
}
