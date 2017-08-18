package com.gianlu.pyxreborn.client.UI.Game;


import com.gianlu.pyxreborn.Events;
import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.*;
import com.gianlu.pyxreborn.Models.Client.CGame;
import com.gianlu.pyxreborn.Models.Client.CPlayer;
import com.gianlu.pyxreborn.Models.Client.CUser;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.Card.PyxCard;
import com.gianlu.pyxreborn.client.UI.Card.PyxCardGroup;
import com.gianlu.pyxreborn.client.UI.ListCells.PlayerCell;
import com.gianlu.pyxreborn.client.UI.ListCells.UserCell;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameUI implements Client.IEventListener, PyxCard.ICard {
    private final Stage mainStage;
    private final Stage chatStage;
    private final Stage stage;
    private final Client client;
    private final CUser me;
    private final ObservableList<CPlayer> playersList;
    private final ObservableList<CUser> spectatorsList;
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
        this.playersList = new ObservableListWrapper<>(game.players);
        this.spectatorsList = new ObservableListWrapper<>(game.spectators);
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
        spectators.setItems(spectatorsList);

        players.setCellFactory(param -> new PlayerCell());
        players.setItems(playersList);

        client.addListener((event, request) -> true, this);
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

    @FXML
    public void gameOptions(MouseEvent event) {
        GameOptionsUI.show(client, me, game);
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
                List<WhiteCard> whiteCards = Utils.toList(obj.getAsJsonArray(Fields.HAND.toString()), WhiteCard.class);
                Platform.runLater(() -> {
                    this.hand.getChildren().clear();
                    for (WhiteCard card : whiteCards)
                        this.hand.getChildren().add(new PyxCardGroup(Collections.singletonList(card), this));
                });
                break;
            case GAME_NEW_ROUND:
                game.status = Game.Status.PLAYING;
                judge = new CPlayer(obj.getAsJsonObject(Fields.JUDGE.toString()));
                BlackCard blackCard = new BlackCard(obj.getAsJsonObject(Fields.BLACK_CARD.toString()));
                Platform.runLater(() -> {
                    this.blackCard.getChildren().clear();
                    this.blackCard.getChildren().add(new PyxCard(blackCard, null));

                    this.playedCards.getChildren().clear();

                    if (Objects.equals(judge.user, me)) { // I am the judge
                        instructions.setText("You're the Card Czar.");
                        hand.setDisable(true);
                    } else { // I am not the judge
                        instructions.setText("Pick " + blackCard.numPick + " card(s) to play...");
                        hand.setDisable(false);
                    }
                });
                break;
            case GAME_JUDGING:
                game.status = Game.Status.JUDGING;

                List<List<WhiteCard>> playedCards = new ArrayList<>();
                for (JsonElement element : obj.getAsJsonArray(Fields.PLAYED_CARDS.toString()))
                    playedCards.add(Utils.toList(element.getAsJsonArray(), WhiteCard.class));

                Platform.runLater(() -> {
                    this.hand.getChildren().clear();
                    this.playedCards.getChildren().clear();
                    for (List<WhiteCard> cards : playedCards) {
                        PyxCardGroup group = new PyxCardGroup(cards, this);
                        this.playedCards.getChildren().add(group);
                    }
                });
                break;
            case GAME_ROUND_ENDED:
                int winningCard = obj.get(Fields.WINNER_CARD_ID.toString()).getAsInt();
                CPlayer winner = new CPlayer(obj.getAsJsonObject(Fields.WINNER.toString()));

                Platform.runLater(() -> {
                    instructions.setText(winner.user.nickname + " wins this round!");
                    for (Node child : this.playedCards.getChildren())
                        if (child instanceof PyxCardGroup)
                            for (WhiteCard card : ((PyxCardGroup) child).cards)
                                if (card.id == winningCard)
                                    ((PyxCardGroup) child).setWinning();
                });
                break;
            case GAME_STOPPED:
                game.status = Game.Status.LOBBY;
                Platform.runLater(() -> {
                    this.blackCard.getChildren().clear();
                    this.playedCards.getChildren().clear();
                    this.hand.getChildren().clear();
                });
                break;
            case GAME_OPTIONS_CHANGED:
                game.options = new CGame.Options(obj.getAsJsonObject(Fields.OPTIONS.toString()));
                break;
            case GAME_PLAYER_STATUS_CHANGED:
                CPlayer player = new CPlayer(obj.getAsJsonObject(Fields.PLAYER.toString()));
                Platform.runLater(() -> {
                    int index = playersList.indexOf(player);
                    if (index != -1) playersList.set(index, player);
                });

                if (Objects.equals(player.user, me) && player.status == Player.Status.WAITING) {
                    Platform.runLater(() -> {
                        instructions.setText("Waiting for other players...");
                        hand.setDisable(true);
                    });
                }
                break;
        }
    }

    @Override
    public void onCardSelected(BaseCard card) {
        if (Objects.equals(me, judge.user) && game.status == Game.Status.JUDGING) {
            JsonObject obj = client.createRequest(Operations.JUDGE);
            obj.addProperty(Fields.GID.toString(), game.gid);
            obj.addProperty(Fields.CARD_ID.toString(), card.id);
            try {
                client.sendMessageBlocking(obj);
            } catch (InterruptedException | PyxException ex) {
                UIClient.notifyException(ex);
            }
        } else if (!Objects.equals(me, judge.user) && game.status == Game.Status.PLAYING) {
            JsonObject obj = client.createRequest(Operations.PLAY_CARD);
            obj.addProperty(Fields.GID.toString(), game.gid);
            obj.addProperty(Fields.CARD_ID.toString(), card.id);
            try {
                client.sendMessageBlocking(obj);
            } catch (InterruptedException | PyxException ex) {
                UIClient.notifyException(ex);
                return;
            }

            for (int i = hand.getChildren().size() - 1; i >= 0; i--) {
                Node node = hand.getChildren().get(i);
                if (node instanceof PyxCardGroup) {
                    List<WhiteCard> cards = ((PyxCardGroup) node).cards;
                    for (WhiteCard c : cards) {
                        if (c.id == card.id) {
                            hand.getChildren().remove(i);
                            break;
                        }
                    }
                }
            }
        }
    }
}
