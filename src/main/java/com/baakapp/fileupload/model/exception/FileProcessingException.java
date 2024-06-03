package com.baakapp.fileupload.model.exception;

public class FileProcessingException extends RuntimeException{

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
