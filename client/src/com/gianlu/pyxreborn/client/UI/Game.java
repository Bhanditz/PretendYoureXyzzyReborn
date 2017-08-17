package com.gianlu.pyxreborn.client.UI;


import com.gianlu.pyxreborn.client.Client;

public class Game { // TODO
    private final Client client;

    public Game(Client client) {
        this.client = client;
    }

    public static void show(Client client, String gameName) {
        UIClient.loadScene(null, gameName + " game - Pretend You're Xyzzy Reborn", "Game.fxml", new Game(client));
    }
}
