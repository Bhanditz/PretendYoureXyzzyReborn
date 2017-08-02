package com.gianlu.pyxreborn.server;

import com.beust.jcommander.Parameter;

public class Config {
    @Parameter(names = {"--server-port"}, description = "Server will listen for request on this port", required = true)
    public int serverPort;

    @Parameter(names = {"--max-users"}, description = "Max number of users that can connect to the server simultaneously")
    public int maxUsers = 2000;
}
