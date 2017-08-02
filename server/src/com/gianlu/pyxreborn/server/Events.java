package com.gianlu.pyxreborn.server;

public enum Events {
    NEW_USER("nu");

    private final String val;

    Events(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
