package com.gianlu.pyxreborn.Exceptions;

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
    GAME_NOT_ENOUGH_CARDS("gnec");

    private final String val;

    ErrorCodes(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
