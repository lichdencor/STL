package io.stl.stl_core.security.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;

/**
 * Configuration for JWT (token) generation and validation.
 * Loads RSA keypair from PEM files for secure token signing.
 */
@Configuration
public class JwtConfig {
  private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);

  @Value("${jwt.private-key:ckasspath:jwt-keys/private_key.pem}")
  private Resource privateKeyResource;

  @Value("${jwt.public-key:classpath:jwt-keys/public_key.pem}")
  private Resource publicKeyResource;

  @Value("${jwt.token-expiration-minutes:60}")
  private long expiration;

  @Value("${jwt.refresh-expiration:86400000}") // 24 hours in milliseconds
  private long refreshExpiration;

  @Value("${jwt.issuer:stl-api}")
  private String issuer;

  private PrivateKey privateKey;
  private PublicKey publicKey;

  @PostConstruct
  public void init() {
    try {
      log.info("Loading JWT keypair...");

      this.privateKey = loadPrivateKey();
      this.publicKey = loadPublicKey();

      log.info("JWT keypair loaded successfully");
      log.info("Token expiration: {} ms ({} minutes)", expiration, expiration / 60000);
      log.info("Refresh token expiration: {} ms ({} hours)", refreshExpiration, refreshExpiration / 3600000);

    } catch (Exception e) {
      log.error("Failed to load JWT keypair", e);
      throw new IllegalStateException("Could not initialize JWT configuration", e);
    }
  }

  /**
   * Loads the RSA private key from PEM file.
   */
  private PrivateKey loadPrivateKey() throws Exception {
    String key = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

    // We remove the PEM headers and footers
    key = key.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replace("-----BEGIN RSA PRIVATE KEY-----", "")
        .replace("-----END RSA PRIVATE KEY-----", "")
        .replaceAll("\\s", "");

    byte[] keyBytes = Base64.getDecoder().decode(key);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    return keyFactory.generatePrivate(spec);
  }

  /**
   * Loads the RSA public key from PEM file.
   */
  private PublicKey loadPublicKey() throws Exception {
    String key = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

    // Remove PEM headers and footers
    key = key.replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replaceAll("\\s", "");

    byte[] keyBytes = Base64.getDecoder().decode(key);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

    return keyFactory.generatePublic(spec);
  }

  // Getters
  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public long getExpiration() {
    return expiration;
  }

  public long getRefreshExpiration() {
    return refreshExpiration;
  }

  public String getIssuer() {
    return issuer;
  }

  /**
   * Gets the signature algorithm used for JWT signing.
   */
  public SignatureAlgorithm getSignatureAlgorithm() {
    return SignatureAlgorithm.RS256; // RSA with SHA-256
  }

}
