package com.gianlu.pyxreborn.client.UI.Card;

import com.gianlu.pyxreborn.Models.WhiteCard;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class PyxCardGroup extends HBox {
    public final List<WhiteCard> cards;

    public PyxCardGroup(List<WhiteCard> cards, PyxCard.ICard listener) {
        this.cards = cards;
        for (WhiteCard card : cards)
            getChildren().add(new PyxCard(card, listener));

        setPadding(new Insets(5));

        if (cards.size() > 1)
            setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(8), BorderWidths.DEFAULT)));
    }

    public void setWinning() {
        for (Node child : getChildren())
            if (child instanceof PyxCard)
                ((PyxCard) child).setWinning();
    }
}
