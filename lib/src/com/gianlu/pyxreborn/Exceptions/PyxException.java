package com.gianlu.pyxreborn.Exceptions;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class PyxException extends Exception {
    public final JsonObject resp;

    public PyxException(JsonObject resp) {
        super(buildMessage(resp.get("ec").getAsString()));
        this.resp = resp;
    }

    @NotNull
    private static String buildMessage(String errorCode) {
        ErrorCodes code = ErrorCodes.parse(errorCode);
        return errorCode + (code != null ? (" -> " + code.name()) : "");
    }
}
