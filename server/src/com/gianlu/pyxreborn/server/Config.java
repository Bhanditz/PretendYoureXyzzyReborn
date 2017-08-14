package com.gianlu.pyxreborn.server;

import com.beust.jcommander.Parameter;

import java.io.File;

public class Config {
    @Parameter(names = {"--server-port"}, description = "Server will listen for request on this port")
    public int serverPort = 6969;

    @Parameter(names = {"--max-users"}, description = "Max number of users that can connect to the server simultaneously")
    public int maxUsers = 2000;

    @Parameter(names = {"--cards-db"}, description = "The SQLite database where the cards are stored", required = true)
    public File cardsDatabase;

    @Parameter(names = {"--max-games"}, description = "Max number of games")
    public int maxGames = 400;

    @Parameter(names = {"--reconnect-delay"}, description = "Delay (in sec) within a disconnected user can recover his session")
    public int reconnectDelay = 30;

    @Parameter(names = {"--intermission"}, description = "Wait time (in sec) between two rounds")
    public int intermission = 8;
}
