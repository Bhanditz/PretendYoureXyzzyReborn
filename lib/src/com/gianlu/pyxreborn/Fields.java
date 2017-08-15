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
    SUCCESSFUL("suc"),
    KICKED("k"),
    IP("ip"),
    HAND("H"),
    TEXT("t"),
    WATERMARK("w"),
    NUM_DRAW("nd"),
    NUM_PICK("np"),
    JUDGE("j"),
    BLACK_CARD("bc"),
    CARD_ID("cid"),
    PLAYED_CARDS("pc"),
    WINNER("W"),
    WINNER_CARD_ID("wci"),
    MAX_PLAYERS("mp"),
    MAX_SPECTATORS("ms"),
    CARD_SET_ID("csid"),
    NAME("n"),
    DESCRIPTION("d"),
    WEIGHT("wh"),
    WHITE_CARD("wc"),
    CARD_SET("cs");

    private final String val;

    Fields(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
