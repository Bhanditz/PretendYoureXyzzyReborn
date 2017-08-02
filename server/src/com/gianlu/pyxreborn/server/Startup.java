package com.gianlu.pyxreborn.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gianlu.pyxreborn.Models.CardSet;
import com.gianlu.pyxreborn.server.DB.CardsDB;

import java.sql.SQLException;
import java.util.List;

public class Startup {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Config config = new Config();

        try {
            JCommander.newBuilder().programName("pyx-reborn")
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
        List<CardSet> sets = db.loadCardSets();

        Server server = new Server(config, sets);
        server.start();
    }
}
