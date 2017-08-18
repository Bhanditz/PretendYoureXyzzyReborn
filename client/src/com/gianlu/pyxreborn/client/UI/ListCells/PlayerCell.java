package com.gianlu.pyxreborn.client.UI.ListCells;

import com.gianlu.pyxreborn.Models.Client.CPlayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class PlayerCell extends ListCell<CPlayer> {

    @Override
    protected void updateItem(CPlayer item, boolean empty) {
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
        private final CPlayer item;
        @FXML
        private Label name;
        @FXML
        private Label score;
        @FXML
        private Label status;

        public Controller(CPlayer item) {
            this.item = item;
        }

        @FXML
        public void initialize() {
            name.setText(item.user.nickname);
            score.setText("Score: " + item.score);
            status.setText("Status: " + item.status.name());
        }
    }
}
