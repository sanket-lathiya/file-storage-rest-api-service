package com.metadata.filestorage.exception;

public class FileNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -5997873540303010948L;

    public FileNotFoundException(String message) {
        super(message);
    }
    
    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
