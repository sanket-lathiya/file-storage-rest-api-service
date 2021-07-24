package com.metadata.filestorage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.metadata.filestorage.response.ResponseMessage;

@ControllerAdvice
public class GenericExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseMessage> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("File too large!"));
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseMessage> handleBadRequestException(BadRequestException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(exc.getMessage()));
    }
    
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleFileNotFoundException(FileNotFoundException exc) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(exc.getMessage()));
    }
    
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ResponseMessage> handleFileStorageException(FileStorageException exc) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(exc.getMessage()));
    }
}