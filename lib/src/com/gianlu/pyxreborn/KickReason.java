package com.gianlu.pyxreborn;

public enum KickReason {
    GAME_EMPTY("ge");

    private final String val;

    KickReason(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
