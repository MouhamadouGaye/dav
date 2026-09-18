package com.digitalassetvault.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

        private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

        @Test
        void shouldReturnConflictForDataIntegrityViolation() {

                DataIntegrityViolationException exception = new DataIntegrityViolationException("duplicate key");

                Map<String, Object> response = handler.handleDataIntegrityViolation(exception);

                assertEquals(409, response.get("status"));
                assertEquals(
                                "RESOURCE_CONFLICT",
                                response.get("error"));
                assertEquals(
                                "Wallet already exists",
                                response.get("message"));
        }

        @Test
        void shouldReturnBadRequestForBusinessValidation() {

                IllegalArgumentException exception = new IllegalArgumentException(
                                "Invalid Ethereum address");

                Map<String, Object> response = handler.handleBusinessValidationException(exception);

                assertEquals(400, response.get("status"));
                assertEquals(
                                "BUSINESS_VALIDATION_ERROR",
                                response.get("error"));
                assertEquals(
                                "Invalid Ethereum address",
                                response.get("message"));
        }
}