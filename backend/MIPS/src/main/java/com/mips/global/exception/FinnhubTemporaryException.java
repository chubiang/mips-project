package com.mips.global.exception;

public class FinnhubTemporaryException extends RuntimeException {

    public FinnhubTemporaryException(String message) {
        super(message);
    }

    public FinnhubTemporaryException(String message, Throwable cause) {
        super(message, cause);
    }
}