package com.gianlu.pyxreborn;

import com.beust.jcommander.Parameter;

public class Config {
    @Parameter(names = {"--server-port"}, description = "Server will listen for request on this port", required = true)
    public int serverPort;
}
