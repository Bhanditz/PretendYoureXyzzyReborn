package com.gianlu.pyxreborn.Exceptions;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public enum ErrorCodes {
    TOO_MANY_USERS("tmu"),
    NICK_ALREADY_IN_USE("naiu"),
    INVALID_REQUEST("ir"),
    NOT_CONNECTED("nc"),
    UNKNOWN_OPERATION("uop"),
    SERVER_ERROR("se"),
    TOO_MANY_GAMES("tmg"),
    ALREADY_IN_GAME("aig"),
    GAME_DOESNT_EXIST("gde"),
    GAME_FULL("gf"),
    INVALID_SID("isid"),
    NOT_GAME_HOST("ngh"),
    GAME_NOT_STARTED("gns"),
    GAME_NOT_ENOUGH_PLAYERS("gnep"),
    GAME_NOT_ENOUGH_CARDS("gnec"),
    NOT_IN_THIS_GAME("nitg"),
    GAME_CARD_NOT_IN_YOUR_HAND("gcniyh"),
    GAME_NOT_YOUR_TURN("gnyt"),
    GAME_CARD_NOT_PLAYED("gcnp"),
    GAME_ALREADY_STARTED("gas"),
    INVALID_CARD_SET_ID("icsid");

    private final String val;

    ErrorCodes(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    @Nullable
    public static ErrorCodes parse(String val) {
        for (ErrorCodes code : values())
            if (Objects.equals(code.val, val))
                return code;

        return null;
    }
}
