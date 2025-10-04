package com.hospedaya.backend.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
    String path,
    String message,
    int statusCode,
    LocalDateTime timestamp,
    List<String> errors
) {
    public ApiError(String path, String message, int statusCode) {
        this(path, message, statusCode, LocalDateTime.now(), List.of());
    }

    public ApiError(String path, String message, int statusCode, List<String> errors) {
        this(path, message, statusCode, LocalDateTime.now(), errors);
    }
}
