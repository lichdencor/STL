package io.stl.stl_core.controller.exception;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard error response structure for API errors.
 * Provides consistent error format across all endpoints.
 */
public class ErrorResponse {

  private String error;
  private String message;
  private int status;
  private OffsetDateTime timestamp;
  private String path;
  private List<FieldError> fieldErrors;

  public ErrorResponse(String error, String message, int status, String path) {
    this.error = error;
    this.message = message;
    this.status = status;
    this.path = path;
    this.timestamp = OffsetDateTime.now();
  }

  public ErrorResponse(String error, String message, int status, String path,
      List<FieldError> fieldErrors) {
    this(error, message, status, path);
    this.fieldErrors = fieldErrors;
  }

  // Getters and Setters
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<FieldError> getFieldErrors() {
    return fieldErrors;
  }

  public void setFieldErrors(List<FieldError> fieldErrors) {
    this.fieldErrors = fieldErrors;
  }

  /**
   * Represents a validation error on a specific field.
   */
  public static class FieldError {
    private String field;
    private Object rejectedValue;
    private String message;

    public FieldError(String field, Object rejectedValue, String message) {
      this.field = field;
      this.rejectedValue = rejectedValue;
      this.message = message;
    }

    public String getField() {
      return field;
    }

    public void setField(String field) {
      this.field = field;
    }

    public Object getRejectedValue() {
      return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
      this.rejectedValue = rejectedValue;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
  }
}
