package com.gianlu.pyxreborn.Exceptions;

public enum ErrorCodes {
    TOO_MANY_USERS("tmu"),
    NICK_ALREADY_IN_USE("naiu"),
    INVALID_REQUEST("ir"),
    NOT_CONNECTED("nc"),
    UNKNOWN_OPERATION("uop");

    private final String val;

    ErrorCodes(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }
}
