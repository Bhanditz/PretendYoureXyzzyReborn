package com.gianlu.pyxreborn.client.UI.ListCells;

import com.gianlu.pyxreborn.Models.Client.CUser;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class UserCell extends ListCell<CUser> {

    @Override
    protected void updateItem(CUser item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) setGraphic(null);
        else setGraphic(new Label(item.nickname));
    }
}
