package com.gianlu.pyxreborn.Models;

import com.gianlu.pyxreborn.Annotations.ClientSafe;
import com.google.gson.JsonObject;

@ClientSafe
public class WhiteCard extends BaseCard {
    public WhiteCard(int id, String text, String watermark) {
        super(id, text, watermark);
    }

    public WhiteCard(JsonObject obj) {
        super(obj);
    }
}
