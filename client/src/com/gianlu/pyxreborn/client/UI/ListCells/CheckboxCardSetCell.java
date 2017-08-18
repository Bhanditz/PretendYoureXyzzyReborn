package com.gianlu.pyxreborn.client.UI.ListCells;

import com.gianlu.pyxreborn.Models.Client.CCompactCardSet;
import com.gianlu.pyxreborn.Models.Client.CGame;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxListCell;

public class CheckboxCardSetCell extends CheckBoxListCell<CCompactCardSet> {
    private final CGame.Options newOptions;
    private final boolean amHost;

    public CheckboxCardSetCell(CGame.Options newOptions, boolean amHost) {
        this.newOptions = newOptions;
        this.amHost = amHost;
    }

    @Override
    public void updateItem(CCompactCardSet item, boolean empty) {
        if (!empty) {
            setSelectedStateCallback(param -> {
                SimpleBooleanProperty property = new SimpleBooleanProperty(newOptions.cardSetIds.contains(item.id));
                property.addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        if (!newOptions.cardSetIds.contains(item.id)) newOptions.cardSetIds.add(item.id);
                    } else {
                        newOptions.cardSetIds.remove((Integer) item.id);
                    }
                });

                return property;
            });
        }

        super.updateItem(item, empty);

        if (!empty) setText(item.name);
        Node node = getGraphic();
        if (node != null) node.setDisable(!amHost);
    }
}
