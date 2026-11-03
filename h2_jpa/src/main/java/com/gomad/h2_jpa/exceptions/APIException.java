package com.gomad.h2_jpa.exceptions;

public class APIException extends RuntimeException {
    private final static  long serialVersionUID = 1L;

    public APIException(String message) {
        super(message);
    }
    public APIException(String message, Throwable cause) {

    }
}
