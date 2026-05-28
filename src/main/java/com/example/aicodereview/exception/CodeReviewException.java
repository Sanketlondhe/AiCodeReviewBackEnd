package com.example.aicodereview.exception;

import org.springframework.http.HttpStatus;

public class CodeReviewException extends RuntimeException {

    private final HttpStatus status;
    private final String userMessage;

    public CodeReviewException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.userMessage = message;
    }

    public CodeReviewException(String message, String userMessage, HttpStatus status) {
        super(message);
        this.status = status;
        this.userMessage = userMessage;
    }

    public CodeReviewException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.userMessage = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getUserMessage() { return userMessage; }
}
