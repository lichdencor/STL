package io.stl.stl_core.controller.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.stl.stl_core.service.TransactionService;
import io.stl.stl_core.service.TransactionValidationService.TransactionValidationException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Global exception handler for all controllers.
 * Converts exceptions to consistent error responses.
 * 
 * Benefits:
 * - Centralized error handling (DRY principle)
 * - Consistent error format across all endpoints
 * - Clean separation from business logic
 * - Proper HTTP status codes
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles validation errors from @Valid annotation.
   * Returns 400 BAD REQUEST with field-level error details.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {

    log.warn("Validation error: {}", ex.getMessage());

    BindingResult bindingResult = ex.getBindingResult();

    List<ErrorResponse.FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
        .map(error -> new ErrorResponse.FieldError(
            error.getField(),
            error.getRejectedValue(),
            error.getDefaultMessage()))
        .collect(Collectors.toList());

    ErrorResponse errorResponse = new ErrorResponse(
        "VALIDATION_ERROR",
        "Invalid request data",
        HttpStatus.BAD_REQUEST.value(),
        request.getRequestURI(),
        fieldErrors);

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /**
   * Handles business validation errors from ValidationService.
   * Returns 400 BAD REQUEST with business rule error message.
   */
  @ExceptionHandler(TransactionValidationException.class)
  public ResponseEntity<ErrorResponse> handleBusinessValidationException(
      TransactionValidationException ex,
      HttpServletRequest request) {

    log.warn("Business validation error: {} - {}", ex.getErrorCode(), ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        ex.getErrorCode(),
        ex.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        request.getRequestURI());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /**
   * Handles transaction not found errors.
   * Returns 404 NOT FOUND.
   */
  @ExceptionHandler(TransactionService.TransactionNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(
      TransactionService.TransactionNotFoundException ex,
      HttpServletRequest request) {

    log.warn("Transaction not found: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "TRANSACTION_NOT_FOUND",
        ex.getMessage(),
        HttpStatus.NOT_FOUND.value(),
        request.getRequestURI());

    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponse);
  }

  /**
   * Handles illegal state errors (e.g., invalid status transitions).
   * Returns 409 CONFLICT.
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(
      IllegalStateException ex,
      HttpServletRequest request) {

    log.warn("Illegal state error: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "INVALID_STATE",
        ex.getMessage(),
        HttpStatus.CONFLICT.value(),
        request.getRequestURI());

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(errorResponse);
  }

  /**
   * Handles illegal argument errors (e.g., invalid UUIDs).
   * Returns 400 BAD REQUEST.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      HttpServletRequest request) {

    log.warn("Illegal argument error: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        "INVALID_ARGUMENT",
        ex.getMessage(),
        HttpStatus.BAD_REQUEST.value(),
        request.getRequestURI());

    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(errorResponse);
  }

  /**
   * Handles all other unexpected exceptions.
   * Returns 500 INTERNAL SERVER ERROR.
   * 
   * IMPORTANT: Never expose internal error details to clients in production.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex,
      HttpServletRequest request) {

    log.error("Unexpected error", ex);

    // In production, don't expose internal error details
    String message = "An unexpected error occurred. Please contact support.";

    // In development, you might want to expose more details
    // message = ex.getMessage();

    ErrorResponse errorResponse = new ErrorResponse(
        "INTERNAL_ERROR",
        message,
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        request.getRequestURI());

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(errorResponse);
  }
}
