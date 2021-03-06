package com.gianlu.pyxreborn.Exceptions;

public class GeneralException extends Exception {
    public final ErrorCodes code;

    public GeneralException(ErrorCodes code) {
        super(code.toString());
        this.code = code;
    }

    public GeneralException(ErrorCodes code, Throwable cause) {
        super(code.toString(), cause);
        this.code = code;
    }
}
