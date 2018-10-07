package com.revolut.denis.exception;

public class OperationException extends RuntimeException {

    private final Reason reason;

    public OperationException(Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }
}
