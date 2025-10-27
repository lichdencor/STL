package io.stl.stl_core.security.jwt;

/**
 * Custom exception for JWT token errors.
 */
public class JwtTokenException extends RuntimeException {
  private final String errorCode;

  public JwtTokenException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public JwtTokenException(String message, String errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

  // specific exception types
  public static class InvalidTokenException extends JwtTokenException {
    public InvalidTokenException(String message) {
      super(message, "INVALID_TOKEN");
    }
  }

  public static class ExpiredTokenException extends JwtTokenException {
    public ExpiredTokenException(String message) {
      super(message, "EXPIRED_TOKEN");
    }
  }

  public static class MissingTokenException extends JwtTokenException {
    public MissingTokenException(String message) {
      super(message, "MISSING_TOKEN");
    }
  }
}
