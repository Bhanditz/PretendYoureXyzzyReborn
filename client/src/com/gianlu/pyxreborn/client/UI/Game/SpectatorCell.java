package com.gianlu.pyxreborn.client.UI.Game;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonObject;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class SpectatorCell extends ListCell<JsonObject> {
    @Override
    protected void updateItem(JsonObject item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) setGraphic(null);
        else setGraphic(new Label(item.get(Fields.NICKNAME.toString()).getAsString()));
    }
}
