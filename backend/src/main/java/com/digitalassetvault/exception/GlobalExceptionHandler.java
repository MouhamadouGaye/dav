

package com.digitalassetvault.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> fieldErrors.put(
                        error.getField(),
                        error.getDefaultMessage()));

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", 400);
        response.put("error", "VALIDATION_ERROR");
        response.put("message", "Request validation failed");
        response.put("fieldErrors", fieldErrors);

        return response;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBusinessValidationException(
            IllegalArgumentException exception) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", 400);
        response.put("error", "BUSINESS_VALIDATION_ERROR");
        response.put("message", exception.getMessage());

        return response;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDataIntegrityViolation(
            DataIntegrityViolationException exception) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("status", 409);
        response.put("error", "RESOURCE_CONFLICT");
        response.put("message", "Wallet already exists");

        return response;
    }
}