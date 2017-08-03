package com.gianlu.pyxreborn.client;

import com.gianlu.pyxreborn.Operations;
import com.google.gson.JsonObject;

import java.net.URI;

public class Startup {
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) throw new IllegalArgumentException("Missing nickname!");
        String nickname = args[0];

        Client client = new Client(URI.create("ws://127.0.0.1:89/"), nickname);
        if (client.connectBlocking()) {
            client.sendMessage(client.createRequest(Operations.GET_GAMES_LIST), new PyxClientAdapter.IMessage() {
                @Override
                public void onMessage(JsonObject resp) {
                    System.out.println(resp);
                }

                @Override
                public void onException(Exception ex) {
                    ex.printStackTrace();
                }
            });
        } else {
            System.out.println("FAILED CONNECTING!");
        }
    }
}
