package com.gianlu.pyxreborn.Exceptions;

import com.google.gson.JsonObject;

public class PyxException extends Exception {
    public final JsonObject resp;

    public PyxException(JsonObject resp) {
        super(resp.get("ec").getAsString());
        this.resp = resp;
    }
}
