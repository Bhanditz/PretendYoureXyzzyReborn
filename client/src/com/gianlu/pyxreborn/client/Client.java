package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Events;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class Client extends PyxClientAdapter {

    public Client(URI serverUri, String nickname, @Nullable String sid) {
        super(serverUri, nickname, sid);
    }

    @Override
    public void onEvent(Events event, JsonObject request) {
        Logger.info(event.name() + " (" + event.toString() + "): " + request);
    }
}
