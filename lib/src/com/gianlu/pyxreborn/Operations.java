package com.gianlu.pyxreborn;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum Operations {
    GET_GAMES_LIST("ggl"),
    GET_USERS_LIST("gul");

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
