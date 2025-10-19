package com.reece.exception;

import com.reece.model.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(RuntimeException ex) {
        return buildResponse(false, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressBookAPIException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(AddressBookAPIException ex) {
        return buildResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildResponse(false, "Validation failed: " + errors, HttpStatus.BAD_REQUEST);
    }

    // Catch-all for unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralError(Exception ex) {
        ex.printStackTrace(); // For debugging, remove in production
        return buildResponse(false, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to build the response
    private ResponseEntity<ApiResponse<Void>> buildResponse(boolean success, String message, HttpStatus status) {
        ApiResponse<Void> response = new ApiResponse<>(success, message, null);
        response.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(response, status);
    }
}
