package com.navikenz.hotelbooking.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage() != null ? ex.getMessage() : "Admin access required");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage() != null ? ex.getMessage() : "Invalid or expired token");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> jsonFieldName(e.getField()) + ": " + e.getDefaultMessage())
                .orElse("Validation failed");
        return build(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    // Translates the Java bean field name (e.g. ratePerDay) to the JSON name the
    // client actually sent (e.g. rate_per_day), using whatever naming strategy the
    // app's ObjectMapper is configured with — so this stays correct if that ever changes,
    // instead of hardcoding snake_case here and drifting out of sync again.
    private String jsonFieldName(String javaFieldName) {
        var strategy = objectMapper.getSerializationConfig().getPropertyNamingStrategy();
        if (strategy instanceof PropertyNamingStrategies.NamingBase namingBase) {
            return namingBase.translate(javaFieldName);
        }
        return javaFieldName;
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String detail) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("detail", detail);
        return ResponseEntity.status(status).body(body);
    }
}
