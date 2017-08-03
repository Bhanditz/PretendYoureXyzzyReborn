package com.gianlu.pyxreborn;

public enum Fields {
    NICKNAME("ni"),
    SESSION_ID("sid"),
    ERROR_CODE("ec"),
    EVENT("ev"),
    ID("id"),
    OPERATION("op"),
    USERS_LIST("ul"),
    MAX_USERS("mu"),
    GAMES_LIST("gl"),
    MAX_GAMES("mg"),
    GID("gid"),
    PLAYERS("p"),
    HOST("h"),
    USER("u"),
    SUCCESSFUL("succ");

    private final String val;

    Fields(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
