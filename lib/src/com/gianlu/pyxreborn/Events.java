package com.gianlu.pyxreborn;

public enum Events {
    NEW_USER("nu"),
    USER_LEFT("ul"),
    NEW_GAME("ng"),
    GAME_NEW_PLAYER("gnp"),
    GAME_PLAYER_LEFT("gpl"),
    GAME_REMOVED("gr");

    private final String val;

    Events(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
