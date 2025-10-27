package io.stl.stl_core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.stl.stl_core.security.dto.LoginRequest;
import io.stl.stl_core.security.dto.LoginResponse;
import io.stl.stl_core.security.service.AuthenticationService;
import io.stl.stl_core.service.dto.response.ApiResponse;
import io.stl.stl_core.security.dto.RefreshTokenRequest;
import jakarta.validation.Valid;

/**
 * REST Controller for authentication operations.
 * Handles login, token refresh, and logout.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

  private final AuthenticationService authenticationService;

  public AuthenticationController(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  /**
   * Login endpoint - authenticates user and returns JWT tokens.
   * 
   * POST /api/v1/auth/login
   * 
   * @param request Login credentials (email, password)
   * @return LoginResponse with access token, refresh token, and user info
   */
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {

    log.info("Login request received for email: {}", request.getEmail());

    LoginResponse response = authenticationService.login(request);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Token refresh endpoint - generates new access token from refresh token.
   * 
   * POST /api/v1/auth/refresh
   * 
   * @param request Refresh token request
   * @return LoginResponse with new access token
   */
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<LoginResponse>> refresh(
      @Valid @RequestBody RefreshTokenRequest request) {

    log.info("Token refresh request received");

    LoginResponse response = authenticationService.refreshToken(request.getRefreshToken());

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Logout endpoint - invalidates token (will be implemented with token blacklist
   * in Phase 4).
   * 
   * POST /api/v1/auth/logout
   * 
   * @param authorizationHeader Authorization header with Bearer token
   * @return Success response
   */
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

    log.info("Logout request received");

    // TODO Phase 4: Implement token blacklist
    // For now, just return success
    // Client should discard the token

    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
