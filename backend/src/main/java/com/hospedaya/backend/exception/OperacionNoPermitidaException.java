package com.hospedaya.backend.exception;

public class OperacionNoPermitidaException extends RuntimeException {

    public OperacionNoPermitidaException(String message) {
        super(message);
    }

    public OperacionNoPermitidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
