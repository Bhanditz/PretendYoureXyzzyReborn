package com.gianlu.pyxreborn.client.UI.Game;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class PlayerCell extends ListCell<JsonObject> {

    @Override
    protected void updateItem(JsonObject item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PlayerCell.fxml"));
            loader.setController(new Controller(item));

            try {
                setGraphic(loader.load());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private class Controller {
        private final JsonObject item;
        @FXML
        private Label name;
        @FXML
        private Label score;
        @FXML
        private Label status;

        public Controller(JsonObject item) {
            this.item = item;
        }

        @FXML
        public void initialize() {
            name.setText(item.getAsJsonObject(Fields.USER.toString()).get(Fields.NICKNAME.toString()).getAsString());
            score.setText("Score: " + item.get(Fields.SCORE.toString()).getAsInt());
            status.setText("Status: " + item.get(Fields.STATUS.toString()).getAsString());
        }
    }
}
