package com.gianlu.pyxreborn.client;

import java.net.URI;

public class Startup {
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) throw new IllegalArgumentException("Missing nickname!");
        String nickname = args[0];

        Client client = new Client(URI.create("ws://127.0.0.1:89/"), nickname);
        client.connectBlocking();
    }
}
