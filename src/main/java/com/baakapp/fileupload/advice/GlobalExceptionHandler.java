package com.baakapp.fileupload.advice;

import com.baakapp.fileupload.model.exception.FileProcessingException;
import com.baakapp.fileupload.model.exception.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileUploadException.class)
    public ProblemDetail handleSizeAndTypeErrors(FileUploadException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("FILE_ERROR");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }
    @ExceptionHandler(FileProcessingException.class)
    public ProblemDetail handleFileProcessingErrors(FileProcessingException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("FILE_PROCESSING_ERROR");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }


    @ExceptionHandler({Exception.class})
    public ProblemDetail handleGenericErrors(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("SYSTEM_ERROR");
        problemDetail.setDetail("The server encountered an error and could not complete your request. Please try again later.");
        return problemDetail;
    }
}
