package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Annotations.ClientSafe;
import com.gianlu.pyxreborn.Fields;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@ClientSafe
public abstract class BaseCard implements Jsonable {
    public final int id;
    public final String text;
    public final String watermark;

    public BaseCard(int id, String text, String watermark) {
        this.id = id;
        this.text = text;
        this.watermark = watermark;
    }

    public BaseCard(JsonObject obj) {
        id = obj.get(Fields.CARD_ID.toString()).getAsInt();
        text = obj.get(Fields.TEXT.toString()).getAsString();
        JsonElement watermarkJson = obj.get(Fields.WATERMARK.toString());
        watermark = watermarkJson.isJsonNull() ? null : watermarkJson.getAsString();
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.ID.toString(), id);
        obj.addProperty(Fields.TEXT.toString(), text);
        obj.addProperty(Fields.WATERMARK.toString(), watermark);
        return obj;
    }
}
