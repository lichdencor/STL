package io.stl.stl_core.security.dto;

/**
 * Response DTO for successful login.
 * Contains JWT tokens and user information.
 */
public class LoginResponse {

  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer";
  private long expiresIn;
  private UserInfo user;

  // Constructors
  public LoginResponse() {
  }

  public LoginResponse(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresIn = expiresIn;
    this.user = user;
  }

  // Getters and Setters
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(long expiresIn) {
    this.expiresIn = expiresIn;
  }

  public UserInfo getUser() {
    return user;
  }

  public void setUser(UserInfo user) {
    this.user = user;
  }

  /**
   * Nested class for user information in login response.
   */
  public static class UserInfo {
    private java.util.UUID id;
    private String email;
    private String name;
    private String role;

    public UserInfo() {
    }

    public UserInfo(java.util.UUID id, String email, String name, String role) {
      this.id = id;
      this.email = email;
      this.name = name;
      this.role = role;
    }

    // Getters and Setters
    public java.util.UUID getId() {
      return id;
    }

    public void setId(java.util.UUID id) {
      this.id = id;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }
  }
}
