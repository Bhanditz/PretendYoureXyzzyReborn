package com.gianlu.pyxreborn;

import com.sun.istack.internal.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static Config instance;
    private final Map<String, String> config;

    private Config() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("server.conf")))) {
            config = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) continue;
                String[] split = line.split("\\s?=\\s?");
                if (split.length < 2) continue;
                config.put(split[0].trim(), split[1].trim());
            }
        }
    }

    /**
     * @return a {@link Config} instance
     */
    public static Config get() {
        if (instance == null) {
            try {
                instance = new Config();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return instance;
    }

    /**
     * The purpose of this method is to instantiated a new {@link Config} instance throwing exceptions
     *
     * @throws IOException if the file, for example, doesn't exist
     */
    public static void instantiate() throws IOException {
        instance = new Config();
    }

    /**
     * Get a String value from the config
     *
     * @param key see {@link Key}
     * @return the desired value, null if it couldn't be found
     */
    @Nullable
    public String getValue(Key key) {
        return config.get(key.val);
    }

    /**
     * Get an int value from the config
     *
     * @param key see {@link Key}
     * @return the desired value
     * @throws NumberFormatException if the value is not an int
     */
    public int getInt(Key key) throws NumberFormatException {
        return Integer.parseInt(getValue(key));
    }

    /**
     * All the configs are defined here
     */
    public enum Key {
        SERVER_PORT("server.port");

        private final String val;

        Key(String val) {
            this.val = val;
        }
    }
}
