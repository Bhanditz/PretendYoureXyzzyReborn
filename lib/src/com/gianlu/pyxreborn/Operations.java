package com.gianlu.pyxreborn;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum Operations {
    LIST_GAMES("llg"),
    LIST_USERS("lu"),
    CREATE_GAME("cg"),
    JOIN_GAME("jg"),
    START_GAME("sg"),
    PLAY_CARD("pc"),
    JUDGE("j"),
    CHANGE_GAME_OPTIONS("cgo"),
    LIST_CARDS("lc"),
    LIST_CARD_SETS("lcs"),
    CHAT("c"),
    GAME_CHAT("gc"),
    GET_GAME("gg"),
    KICK("k"),
    GET_ME("gme"),
    LEAVE_GAME("lg"), STOP_GAME("stg");

    private final String val;

    Operations(String val) {
        this.val = val;
    }

    @Nullable
    public static Operations parse(String val) {
        for (Operations op : values())
            if (Objects.equals(op.val, val))
                return op;

        return null;
    }

    @Override
    public String toString() {
        return val;
    }
}
