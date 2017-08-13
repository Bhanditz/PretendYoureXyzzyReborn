package com.gianlu.pyxreborn.client;

import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class Client extends PyxClientAdapter {

    public Client(URI serverUri, String nickname, @Nullable String sid) {
        super(serverUri, nickname, sid);
    }
}
