package com.gianlu.pyxreborn.client.UI.Game;

import com.gianlu.pyxreborn.Exceptions.PyxException;
import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Client.CCompactCardSet;
import com.gianlu.pyxreborn.Models.Client.CGame;
import com.gianlu.pyxreborn.Models.Client.CUser;
import com.gianlu.pyxreborn.Operations;
import com.gianlu.pyxreborn.Utils;
import com.gianlu.pyxreborn.client.Client;
import com.gianlu.pyxreborn.client.UI.ListCells.CheckboxCardSetCell;
import com.gianlu.pyxreborn.client.UI.NumberStringFilteredConverter;
import com.gianlu.pyxreborn.client.UI.UIClient;
import com.google.gson.JsonObject;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class GameOptionsUI {
    private final Stage stage;
    private final Client client;
    private final CUser me;
    private final CGame game;
    private CGame.Options newOptions;
    @FXML
    private TextField maxPlayers;
    @FXML
    private TextField maxSpectators;
    @FXML
    private ListView<CCompactCardSet> cardSets;
    @FXML
    private Button apply;

    public GameOptionsUI(Stage stage, Client client, CUser me, CGame game) {
        this.stage = stage;
        this.client = client;
        this.me = me;
        this.game = game;
        this.newOptions = new CGame.Options(game.options);
    }

    public static void show(Client client, CUser me, CGame game) {
        Stage stage = new Stage();
        UIClient.loadScene(stage, game.host.nickname + " game options - Pretend You're Xyzzy Reborn", "GameOptions.fxml", new GameOptionsUI(stage, client, me, game));
    }

    @FXML
    public void initialize() {
        NumberStringFilteredConverter numberFormatter = new NumberStringFilteredConverter();
        maxPlayers.setTextFormatter(new TextFormatter<>(numberFormatter, 0, numberFormatter.getFilter()));
        maxPlayers.setText(String.valueOf(game.options.maxPlayers));
        maxPlayers.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) newOptions.maxPlayers = Integer.parseInt(newValue);
        });
        maxSpectators.setTextFormatter(new TextFormatter<>(numberFormatter, 0, numberFormatter.getFilter()));
        maxSpectators.setText(String.valueOf(game.options.maxSpectators));
        maxSpectators.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) newOptions.maxSpectators = Integer.parseInt(newValue);
        });

        List<CCompactCardSet> allCardSets;
        try {
            allCardSets = Utils.toList(client.sendMessageBlocking(client.createRequest(Operations.LIST_CARD_SETS)).getAsJsonArray(Fields.CARD_SET.toString()), CCompactCardSet.class);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        boolean amHost = Objects.equals(game.host, me);
        cardSets.setCellFactory(param -> new CheckboxCardSetCell(newOptions, amHost));
        cardSets.setItems(new ObservableListWrapper<>(allCardSets));

        maxPlayers.setEditable(amHost);
        maxSpectators.setEditable(amHost);
        apply.setDisable(!amHost);
    }

    @FXML
    public void apply(MouseEvent event) {
        JsonObject obj = client.createRequest(Operations.CHANGE_GAME_OPTIONS);
        obj.addProperty(Fields.GID.toString(), game.gid);
        obj.add(Fields.OPTIONS.toString(), newOptions.toJson());

        try {
            client.sendMessageBlocking(obj);
        } catch (InterruptedException | PyxException ex) {
            UIClient.notifyException(ex);
            return;
        }

        stage.close();
    }
}
