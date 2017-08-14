package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonObject;

public class BlackCard extends BaseCard {
    public final int numDraw;
    public final int numPick;

    public BlackCard(int id, String text, String watermark, int numDraw, int numPick) {
        super(id, text, watermark);
        this.numDraw = numDraw;
        this.numPick = numPick;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = super.toJson();
        obj.addProperty(Fields.NUM_DRAW.toString(), numDraw);
        obj.addProperty(Fields.NUM_PICK.toString(), numPick);
        return obj;
    }
}
