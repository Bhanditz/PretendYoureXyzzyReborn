package com.gianlu.pyxreborn;

import java.io.IOException;

public class Startup {
    public static void main(String[] args) {
        try {
            Config.instantiate();
        } catch (IOException ex) {
            System.err.println("The configuration file couldn't be read!");
            System.exit(1);
            return;
        }

        Server server = new Server();
        server.start();
    }
}
