package com.gianlu.pyxreborn;

public enum Fields {
    NICKNAME("ni"),
    SESSION_ID("sid"),
    ERROR_CODE("ec"),
    EVENT("ev"),
    ID("id");

    private final String val;

    Fields(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
