package com.gianlu.pyxreborn;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Startup {
    public static void main(String[] args) {
        Config config = new Config();

        try {
            JCommander.newBuilder()
                    .addObject(config)
                    .build()
                    .parse(args);
        } catch (ParameterException ex) {
            ex.getJCommander().usage();
            System.exit(1);
            return;
        }

        Server server = new Server(config);
        server.start();
    }
}
