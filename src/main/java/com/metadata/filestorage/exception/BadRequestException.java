package com.metadata.filestorage.exception;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 5081587822721771683L;

    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
