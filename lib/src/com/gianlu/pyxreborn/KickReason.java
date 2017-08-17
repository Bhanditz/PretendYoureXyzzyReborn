package com.gianlu.pyxreborn;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum KickReason {
    GAME_EMPTY("ge"),
    GENERAL_KICK("gk"),
    ADMIN_LOGGED("al");

    private final String val;

    KickReason(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    @Nullable
    public static KickReason parse(String val) {
        for (KickReason reason : values())
            if (Objects.equals(reason.val, val))
                return reason;

        return null;
    }
}
