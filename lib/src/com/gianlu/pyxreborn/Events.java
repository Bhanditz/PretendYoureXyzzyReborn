package com.gianlu.pyxreborn;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum Events {
    NEW_USER("nu"),
    USER_LEFT("ul"),
    NEW_GAME("ng"),
    GAME_NEW_PLAYER("gnp"),
    GAME_PLAYER_LEFT("gpl"),
    GAME_REMOVED("gr"),
    GAME_HAND_CHANGED("ghc"),
    GAME_NEW_ROUND("gnr"),
    GAME_JUDGING("gj"),
    GAME_ROUND_ENDED("gre");

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
