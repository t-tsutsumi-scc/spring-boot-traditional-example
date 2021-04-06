package app.controller;

import app.data.controller.response.ErrorResponse;
import app.exception.CustomResponseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler extends ErrorHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex, WebRequest request) {
        return handle(ex, request, (status, errorResponse) -> new ResponseEntity<>(errorResponse, status));
    }

    @ExceptionHandler(CustomResponseException.class)
    public ResponseEntity<?> handle(CustomResponseException ex, WebRequest request) {
        return ex.getResponseEntity();
    }

}
