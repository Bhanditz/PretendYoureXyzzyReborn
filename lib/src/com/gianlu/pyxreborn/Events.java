package com.gianlu.pyxreborn;

public enum Events {
    NEW_USER("nu"),
    USER_LEFT("ul"),
    NEW_GAME("ng"),
    NEW_PLAYER("np");

    private final String val;

    Events(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
