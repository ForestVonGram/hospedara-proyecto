package com.hospedaya.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/pagos")) {
            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("error", "Datos inválidos");
            body.put("mensaje", "Los datos del pago no son válidos o están incompletos");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getErrors()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/pagos")) {
            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("error", "Datos inválidos");
            body.put("mensaje", "Los datos del pago no son válidos o están incompletos");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "Error de validación",
                HttpStatus.BAD_REQUEST.value(),
                errors
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiError> handleDatabaseException(
            DatabaseException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ApiError> handleInternalServerErrorException(
            InternalServerErrorException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailableException(
            ServiceUnavailableException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(OperacionNoPermitidaException.class)
    public ResponseEntity<ApiError> handleOperacionNoPermitidaException(
            OperacionNoPermitidaException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PagoFallidoException.class)
    public ResponseEntity<ApiError> handlePagoFallidoException(
            PagoFallidoException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReservaInvalidaException.class)
    public ResponseEntity<ApiError> handleReservaInvalidaException(
            ReservaInvalidaException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(
            NullPointerException ex,
            HttpServletRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Solicitud inválida";
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                message,
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(
            jakarta.persistence.EntityNotFoundException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado",
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.orm.jpa.JpaObjectRetrievalFailureException.class)
    public ResponseEntity<ApiError> handleJpaObjectRetrievalFailureException(
            org.springframework.orm.jpa.JpaObjectRetrievalFailureException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : "Recurso no encontrado",
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleUnsupportedMediaType(
            org.springframework.web.HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "Content-Type no soportado",
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleNotReadable(
            org.springframework.http.converter.HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/pagos")) {
            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("error", "Datos inválidos");
            body.put("mensaje", "Los datos del pago no son válidos o están incompletos");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "Cuerpo JSON inválido o ausente",
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                "Error interno del servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
