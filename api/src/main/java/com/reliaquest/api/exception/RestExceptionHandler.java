package com.reliaquest.api.exception;

import com.reliaquest.api.model.Response;
import com.reliaquest.api.model.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<Response<Void>> handleNotFoundException(HttpClientErrorException.NotFound ex) {
        log.warn("Employee not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Employee not found");
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Response<Void>> handleHttpClientErrorException(HttpClientErrorException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        log.error("HTTP client error from Mock API: {} - {}", ex.getStatusCode(), ex.getMessage());
        return buildErrorResponse(status, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Response<Void>> buildErrorResponse(HttpStatus status, String message) {
        Response<Void> response = new Response<>();
        response.setStatus(Status.ERROR);
        response.setError(message);
        return ResponseEntity.status(status).body(response);
    }
}
