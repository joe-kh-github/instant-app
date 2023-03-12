package com.instantsystem.instantapp.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InstantException extends RuntimeException {

    @Getter
    private HttpStatus httpStatus;

    public InstantException(String message, HttpStatus httpStatus, Throwable throwable) {
        super(message, throwable);
        this.httpStatus = httpStatus;
    }

    public InstantException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
