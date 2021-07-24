package com.metadata.filestorage.exception;

public class FileStorageException extends RuntimeException {

    private static final long serialVersionUID = -7904638670040735833L;

    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}