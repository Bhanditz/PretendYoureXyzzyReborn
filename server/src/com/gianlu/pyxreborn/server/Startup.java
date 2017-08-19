package com.gianlu.pyxreborn.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gianlu.pyxreborn.server.DB.CardsDB;
import com.gianlu.pyxreborn.server.Lists.CardSets;

import java.sql.SQLException;

public class Startup {
    public static void main(String[] args) throws SQLException {
        Config config = new Config();

        try {
            JCommander.newBuilder()
                    .programName("pyx-server")
                    .addObject(config)
                    .expandAtSign(true)
                    .build()
                    .parse(args);
        } catch (ParameterException ex) {
            ex.printStackTrace();
            ex.getJCommander().usage();
            System.exit(1);
            return;
        }

        CardsDB db = new CardsDB(config);
        CardSets sets = db.loadCardSets();

        Server server = new Server(config, sets);
        server.start();
    }
}
