package com.gianlu.pyxreborn.Models.Client;

import com.gianlu.pyxreborn.Fields;
import com.gianlu.pyxreborn.Models.Jsonable;
import com.google.gson.JsonObject;

public class CCompactCardSet implements Jsonable {
    public final int id;
    public final String name;
    public final String description;
    public final int weight;

    public CCompactCardSet(JsonObject obj) {
        id = obj.get(Fields.CARD_SET_ID.toString()).getAsInt();
        name = obj.get(Fields.NAME.toString()).getAsString();
        description = obj.get(Fields.DESCRIPTION.toString()).getAsString();
        weight = obj.get(Fields.WEIGHT.toString()).getAsInt();
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty(Fields.CARD_SET_ID.toString(), id);
        obj.addProperty(Fields.NAME.toString(), name);
        obj.addProperty(Fields.DESCRIPTION.toString(), description);
        obj.addProperty(Fields.WEIGHT.toString(), weight);
        return obj;
    }
}
