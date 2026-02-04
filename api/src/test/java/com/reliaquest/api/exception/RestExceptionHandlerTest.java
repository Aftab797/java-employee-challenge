package com.reliaquest.api.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.Response;
import com.reliaquest.api.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

class RestExceptionHandlerTest {

    private RestExceptionHandler restExceptionHandler;

    @BeforeEach
    void setUp() {
        restExceptionHandler = new RestExceptionHandler();
    }

    @Test
    @DisplayName("handleNotFoundException - should return 404 with error message")
    void handleNotFoundException_shouldReturn404WithErrorMessage() {
        HttpClientErrorException.NotFound exception = mock(HttpClientErrorException.NotFound.class);
        when(exception.getMessage()).thenReturn("404 Not Found");

        ResponseEntity<Response<Void>> response = restExceptionHandler.handleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Status.ERROR, response.getBody().getStatus());
        assertEquals("Employee not found", response.getBody().getError());
    }

    @Test
    @DisplayName("handleHttpClientErrorException - should return appropriate status code for 429 Too Many Requests")
    void handleHttpClientErrorException_shouldReturn429ForTooManyRequests() {
        HttpClientErrorException exception = mock(HttpClientErrorException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.TOO_MANY_REQUESTS);
        when(exception.getMessage()).thenReturn("429 Too Many Requests");

        ResponseEntity<Response<Void>> response = restExceptionHandler.handleHttpClientErrorException(exception);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Status.ERROR, response.getBody().getStatus());
    }

    @Test
    @DisplayName("handleGenericException - should return 500 with error message")
    void handleGenericException_shouldReturn500WithErrorMessage() {
        Exception exception = new RuntimeException("Something went wrong");

        ResponseEntity<Response<Void>> response = restExceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Status.ERROR, response.getBody().getStatus());
        assertEquals("Something went wrong", response.getBody().getError());
    }
}
