package com.example.movies_recommendation_API.response;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController  {


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        ResponseError error = new ResponseError();
        error.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        ResponseError error = new ResponseError();
        error.setMessage("Tài nguyên yêu cầu không có trên máy chủ.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessages = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessages.append(error.getDefaultMessage()).append(" ")
        );
        ResponseError error = new ResponseError();
        error.setMessage(errorMessages.toString().substring(0, errorMessages.toString().length() - 1));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?> handleMessagingException(MessagingException e) {
        ResponseError error = new ResponseError();
        error.setMessage("Lỗi không gửi được OTP.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
