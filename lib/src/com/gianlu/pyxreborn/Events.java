package com.gianlu.pyxreborn;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum Events {
    CHAT("c"),
    NEW_USER("nu"),
    USER_LEFT("ul"),
    NEW_GAME("ng"),
    GAME_CHAT("gc"),
    GAME_NEW_PLAYER("gnp"),
    GAME_PLAYER_LEFT("gpl"),
    GAME_REMOVED("gr"),
    GAME_HAND_CHANGED("ghc"),
    GAME_NEW_ROUND("gnr"),
    GAME_JUDGING("gj"),
    GAME_ROUND_ENDED("gre"),
    GAME_STOPPED("gs"),
    GAME_OPTIONS_CHANGED("goc"),
    GAME_PLAYER_STATUS_CHANGED("gpsc"),
    GAME_NEW_SPECTATOR("gns"),
    GAME_SPECTATOR_LEFT("gsl");

    private final String val;

    Events(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    @Nullable
    public static Events parse(String val) {
        for (Events event : values())
            if (Objects.equals(event.val, val))
                return event;

        return null;
    }
}
