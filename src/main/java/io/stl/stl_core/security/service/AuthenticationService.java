package io.stl.stl_core.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.stl.stl_core.model.entity.User;
import io.stl.stl_core.repository.UserRepository;
import io.stl.stl_core.security.config.JwtConfig;
import io.stl.stl_core.security.dto.LoginRequest;
import io.stl.stl_core.security.dto.LoginResponse;
import io.stl.stl_core.security.dto.UserPrincipal;
import io.stl.stl_core.security.jwt.JwtTokenProvider;

/**
 * Service for handling authentication operations.
 * Manages login, token generation, and account security.
 */
@Service
public class AuthenticationService {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtConfig jwtConfig;

  public AuthenticationService(UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider,
      JwtConfig jwtConfig) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
    this.jwtConfig = jwtConfig;
  }

  /**
   * Authenticates a user and returns JWT tokens.
   * 
   * @param request Login credentials
   * @return LoginResponse with access and refresh tokens
   * @throws BadCredentialsException if credentials are invalid
   * @throws LockedException         if account is locked
   * @throws DisabledException       if account is disabled
   */
  @Transactional
  public LoginResponse login(LoginRequest request) {
    log.info("Login attempt for email: {}", request.getEmail());

    // 1. Find user by email
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> {
          log.warn("Login failed: User not found - {}", request.getEmail());
          return new BadCredentialsException("Invalid email or password");
        });

    // 2. Check if account is enabled
    if (!user.getEnabled()) {
      log.warn("Login failed: Account disabled - {}", request.getEmail());
      throw new DisabledException("Account is disabled");
    }

    // 3. Check if account is locked
    if (user.isAccountLocked()) {
      log.warn("Login failed: Account locked - {}", request.getEmail());
      throw new LockedException("Account is locked until " + user.getAccountLockedUntil());
    }

    // 4. Validate password
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
      log.warn("Login failed: Invalid password - {}", request.getEmail());

      // Record failed attempt
      user.recordFailedLogin();
      userRepository.save(user);

      if (user.isAccountLocked()) {
        throw new LockedException("Account locked due to too many failed attempts");
      }

      throw new BadCredentialsException("Invalid email or password");
    }

    // 5. Successful login - generate tokens
    log.info("Login successful for user: {}", request.getEmail());

    user.recordSuccessfulLogin();
    userRepository.save(user);

    UserPrincipal principal = UserPrincipal.create(user);

    String accessToken = jwtTokenProvider.generateAccessToken(principal);
    String refreshToken = jwtTokenProvider.generateRefreshToken(principal);

    // 6. Build response
    LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getRole().name());

    return new LoginResponse(
        accessToken,
        refreshToken,
        jwtConfig.getExpiration(),
        userInfo);
  }

  /**
   * Refreshes an access token using a valid refresh token.
   * 
   * @param refreshToken The refresh token
   * @return LoginResponse with new access token
   * @throws BadCredentialsException if refresh token is invalid
   */
  @Transactional(readOnly = true)
  public LoginResponse refreshToken(String refreshToken) {
    log.info("Token refresh attempt");

    // 1. Validate refresh token
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      log.warn("Token refresh failed: Invalid refresh token");
      throw new BadCredentialsException("Invalid refresh token");
    }

    // 2. Check token type
    String tokenType = jwtTokenProvider.getTokenType(refreshToken);
    if (!"refresh".equals(tokenType)) {
      log.warn("Token refresh failed: Not a refresh token");
      throw new BadCredentialsException("Token is not a refresh token");
    }

    // 3. Extract user principal
    UserPrincipal principal = jwtTokenProvider.extractUserPrincipal(refreshToken);

    // 4. Verify user still exists and is enabled
    User user = userRepository.findById(principal.getId())
        .orElseThrow(() -> new BadCredentialsException("User not found"));

    if (!user.getEnabled()) {
      throw new DisabledException("Account is disabled");
    }

    if (user.isAccountLocked()) {
      throw new LockedException("Account is locked");
    }

    // 5. Generate new access token
    String newAccessToken = jwtTokenProvider.generateAccessToken(principal);

    log.info("Token refreshed successfully for user: {}", user.getEmail());

    LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getRole().name());

    return new LoginResponse(
        newAccessToken,
        refreshToken, // Return same refresh token
        jwtConfig.getExpiration(),
        userInfo);
  }

  /**
   * Registers a new user (will be expanded in future sprints).
   * 
   * @param user        User to register
   * @param rawPassword Plain text password
   * @return Registered user
   */
  @Transactional
  public User registerUser(User user, String rawPassword) {
    log.info("Registering new user: {}", user.getEmail());

    // Check if email already exists
    if (userRepository.existsByEmail(user.getEmail())) {
      throw new IllegalArgumentException("Email already registered");
    }

    // Hash password
    user.setPasswordHash(passwordEncoder.encode(rawPassword));
    user.setEnabled(true);
    user.setFailedLoginAttempts(0);

    User saved = userRepository.save(user);
    log.info("User registered successfully: {}", saved.getEmail());

    return saved;
  }
}
