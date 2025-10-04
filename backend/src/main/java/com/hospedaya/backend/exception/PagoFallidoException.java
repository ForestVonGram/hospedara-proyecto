package com.hospedaya.backend.exception;

public class PagoFallidoException extends RuntimeException {

    public PagoFallidoException(String message) {
        super(message);
    }

    public PagoFallidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
