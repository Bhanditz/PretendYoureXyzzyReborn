package com.gianlu.pyxreborn.client.UI.Card;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class PyxCard extends VBox {
    private final JsonObject obj;
    @FXML
    private Label text;
    @FXML
    private Label watermark;

    public PyxCard(JsonObject obj) {
        this.obj = obj;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PyxCard.fxml"));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    public void initialize() {
        this.text.setText(obj.get(Fields.TEXT.toString()).getAsString());
        JsonElement watermark = obj.get(Fields.WATERMARK.toString());
        this.watermark.setText(watermark.isJsonNull() ? null : watermark.getAsString());

        if (obj.has(Fields.NUM_PICK.toString())) { // Black card
            setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
            this.text.setTextFill(Color.WHITE);
            this.watermark.setTextFill(Color.WHITE);
        } else { // White card
            setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            this.text.setTextFill(Color.BLACK);
            this.watermark.setTextFill(Color.BLACK);
        }
    }
}
