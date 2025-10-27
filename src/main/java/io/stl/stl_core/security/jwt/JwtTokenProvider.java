package io.stl.stl_core.security.jwt;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.stl.stl_core.model.enums.UserRole;
import io.stl.stl_core.security.config.JwtConfig;
import io.stl.stl_core.security.dto.UserPrincipal;

/**
 * Service for generating and validating JWT tokens.
 * Uses RSA keys for secure signing and verification.
 */
@Component
public class JwtTokenProvider {
  private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

  private final JwtConfig jwtConfig;

  public JwtTokenProvider(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  /**
   * Generates an access token for a user.
   * 
   * @param userPrincipal The authenticated user
   * @return JWT access token as string
   */
  public String generateAccessToken(UserPrincipal userPrincipal) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

    return Jwts.builder()
        .setSubject(userPrincipal.getId().toString())
        .setIssuer(jwtConfig.getIssuer())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .claim("email", userPrincipal.getEmail())
        .claim("role", userPrincipal.getRole().name())
        .claim("type", "access")
        .signWith(jwtConfig.getPrivateKey(), jwtConfig.getSignatureAlgorithm())
        .compact();
  }

  /**
   * Generates a refresh token for a user.
   * Refresh tokens have longer expiration and can be used to obtain new access
   * tokens.
   * 
   * @param userPrincipal The authenticated user
   * @return JWT refresh token as string
   */
  public String generateRefreshToken(UserPrincipal userPrincipal) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshExpiration());

    return Jwts.builder()
        .setSubject(userPrincipal.getId().toString())
        .setIssuer(jwtConfig.getIssuer())
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .claim("type", "refresh")
        .signWith(jwtConfig.getPrivateKey(), jwtConfig.getSignatureAlgorithm())
        .compact();
  }

  /**
   * Validates a JWT token.
   * 
   * @param token The JWT token to validate
   * @return true if token is valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(jwtConfig.getPublicKey())
          .build()
          .parseSignedClaims(token);
      return true;

    } catch (SignatureException ex) {
      log.error("Invalid JWT signature: {}", ex.getMessage());
    } catch (MalformedJwtException ex) {
      log.error("Invalid JWT token: {}", ex.getMessage());
    } catch (ExpiredJwtException ex) {
      log.error("Expired JWT token: {}", ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      log.error("Unsupported JWT token: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims string is empty: {}", ex.getMessage());
    }

    return false;
  }

  /**
   * Extracts the user ID from a JWT token.
   * 
   * @param token The JWT token
   * @return User UUID
   */
  public UUID getUserIdFromToken(String token) {
    Claims claims = getClaims(token);
    return UUID.fromString(claims.getSubject());
  }

  /**
   * Extracts the email from a JWT token.
   * 
   * @param token The JWT token
   * @return User email
   */
  public String getEmailFromToken(String token) {
    Claims claims = getClaims(token);
    return claims.get("email", String.class);
  }

  /**
   * Extracts the role from a JWT token.
   * 
   * @param token The JWT token
   * @return User role
   */
  public UserRole getRoleFromToken(String token) {
    Claims claims = getClaims(token);
    String roleStr = claims.get("role", String.class);
    return UserRole.valueOf(roleStr);
  }

  /**
   * Extracts the token type (access or refresh).
   * 
   * @param token The JWT token
   * @return Token type
   */
  public String getTokenType(String token) {
    Claims claims = getClaims(token);
    return claims.get("type", String.class);
  }

  /**
   * Checks if a token is expired.
   * 
   * @param token The JWT token
   * @return true if expired, false otherwise
   */
  public boolean isTokenExpired(String token) {
    try {
      Claims claims = getClaims(token);
      return claims.getExpiration().before(new Date());
    } catch (ExpiredJwtException e) {
      return true;
    }
  }

  /**
   * Gets the expiration date from a token.
   * 
   * @param token The JWT token
   * @return Expiration date
   */
  public Date getExpirationFromToken(String token) {
    Claims claims = getClaims(token);
    return claims.getExpiration();
  }

  /**
   * Extracts all claims from a JWT token.
   * 
   * @param token The JWT token
   * @return JWT claims
   * @throws JwtException if token is invalid
   */
  private Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(jwtConfig.getPublicKey())
        .build()
        .parseSignedClaims(token).getBody();

  }

  /**
   * Creates a UserPrincipal from a JWT token.
   * 
   * @param token The JWT token
   * @return UserPrincipal object
   */
  public UserPrincipal extractUserPrincipal(String token) {
    UUID userId = getUserIdFromToken(token);
    String email = getEmailFromToken(token);
    UserRole role = getRoleFromToken(token);

    return new UserPrincipal(userId, email, role);
  }
}
