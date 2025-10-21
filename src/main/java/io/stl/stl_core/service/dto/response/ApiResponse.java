package io.stl.stl_core.service.dto.response;

import java.time.OffsetDateTime;

/**
 * Standard API response wrapper for consistent client responses.
 * Wraps both success and error responses in a uniform structure.
 *
 * @Param <T> The type of the data being returned.
 */

public class ApiResponse<T> {

  private boolean success;
  private T data;
  private ApiError error;
  private OffsetDateTime timestamp;

  // Private constructor to enforce use of static factory methods
  private ApiResponse(boolean success, T data, ApiError error) {
    this.success = success;
    this.data = data;
    this.error = error;
    this.timestamp = OffsetDateTime.now();
  }

  /**
   * Create a successful response with data.
   */
  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, data, null);
  }

  /**
   * Craete an error response.
   */
  public static <T> ApiResponse<T> error(String code, String message) {
    ApiError error = new ApiError(code, message);
    return new ApiResponse<>(false, null, error);
  }

  /**
   * Create an error response with details.
   */
  public static <T> ApiResponse<T> error(String code, String message, Object details) {
    ApiError error = new ApiError(code, message, details);
    return new ApiResponse<>(false, null, error);
  }

  // Getters
  public boolean isSuccess() {
    return success;
  }

  public T getData() {
    return data;
  }

  public ApiError getError() {
    return error;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Inner class for error information.
   */
  public static class ApiError {
    private String code;
    private String message;
    private Object details;

    public ApiError(String code, String message) {
      this.code = code;
      this.message = message;
    }

    public ApiError(String code, String message, Object details) {
      this.code = code;
      this.message = message;
      this.details = details;
    }

    public String getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }

    public Object getDetails() {
      return details;
    }
  }

}
