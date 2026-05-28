package com.example.aicodereview.exception;


import com.example.aicodereview.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── 1. Validation errors (e.g. @NotBlank, @Size) ──
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        log.warn("Validation failed for request [{}]: {}", request.getRequestURI(), details);

        return ResponseEntity.badRequest().body(
                ApiErrorResponse.withDetails(
                        400, "Validation Failed",
                        "Request contains invalid fields",
                        details,
                        request.getRequestURI()
                )
        );
    }

    // ── 2. Our own domain exception ──
    @ExceptionHandler(CodeReviewException.class)
    public ResponseEntity<ApiErrorResponse> handleCodeReviewException(
            CodeReviewException ex,
            HttpServletRequest request) {

        log.error("CodeReviewException [{}] at [{}]: {}",
                ex.getStatus(), request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(ex.getStatus()).body(
                ApiErrorResponse.of(
                        ex.getStatus().value(),
                        ex.getStatus().getReasonPhrase(),
                        ex.getUserMessage(),
                        request.getRequestURI()
                )
        );
    }

    // ── 3. OpenAI / Spring AI API failures ──
    @ExceptionHandler(org.springframework.ai.retry.NonTransientAiException.class)
    public ResponseEntity<ApiErrorResponse> handleAiException(
            Exception ex,
            HttpServletRequest request) {

        log.error("AI provider error at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                ApiErrorResponse.of(
                        502, "AI Provider Error",
                        "The AI service returned an error. Please check your API key or try again.",
                        request.getRequestURI()
                )
        );
    }

    // ── 4. 404 Not Found ──
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {

        log.warn("404 Not Found: [{}]", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiErrorResponse.of(404, "Not Found",
                        "Endpoint not found: " + request.getRequestURI(),
                        request.getRequestURI())
        );
    }

    // ── 5. Illegal argument (bad input we detect manually) ──
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("IllegalArgumentException at [{}]: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.badRequest().body(
                ApiErrorResponse.of(400, "Bad Request", ex.getMessage(), request.getRequestURI())
        );
    }

    // ── 6. Catch-all — never expose internals to the client ──
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiErrorResponse.of(
                        500, "Internal Server Error",
                        "Something went wrong. Please try again later.",
                        request.getRequestURI()
                )
        );
    }
}
