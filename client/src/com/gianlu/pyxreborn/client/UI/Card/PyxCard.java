package com.gianlu.pyxreborn.client.UI.Card;

import com.gianlu.pyxreborn.Models.BaseCard;
import com.gianlu.pyxreborn.Models.BlackCard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public class PyxCard extends GridPane {
    private final BaseCard card;
    @FXML
    private Label text;
    @FXML
    private Label watermark;

    public PyxCard(BaseCard card) {
        this.card = card;
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
        this.text.setText(card.text);
        this.watermark.setText(card.watermark);

        if (card instanceof BlackCard) { // Black card
            setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(8), Insets.EMPTY)));
            this.text.setTextFill(Color.WHITE);
            this.watermark.setTextFill(Color.WHITE);
        } else { // White card
            setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
            this.text.setTextFill(Color.BLACK);
            this.watermark.setTextFill(Color.BLACK);
        }
    }
}
