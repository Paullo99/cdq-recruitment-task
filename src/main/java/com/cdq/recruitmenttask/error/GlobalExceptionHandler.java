package com.cdq.recruitmenttask.error;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Swagger tries to document @RestControllerAdvice classes by default.
// This causes runtime issues in Spring Boot 3+ (NoSuchMethodError).
// Adding @Hidden prevents Swagger from processing this class.
// See: https://stackoverflow.com/questions/79274106/how-to-use-both-restcontrolleradvice-and-swagger-ui-in-spring-boot
@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                ex.getErrorCode().name(),
                ex.getMessage()
        );
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        ApiError error = new ApiError(
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                ex.getMessage()
        );
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}