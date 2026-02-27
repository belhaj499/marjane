package com.fragparfum.Marjane.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class CloudinaryOperationException extends RuntimeException {
    public CloudinaryOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudinaryOperationException(String message) {
        super(message);
    }
}
