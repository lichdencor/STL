package io.stl.stl_core.model.entity;

import io.stl.stl_core.model.enums.*;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a system user who can participate in transactions.
 * Enhanced with authentication fields for Sprint 3.
 */
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name;

  @NotBlank
  @Email
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private UserRole role;

  // Authentication fields (added in Sprint 3)
  @Column(name = "password_hash")
  private String passwordHash;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  @Column(name = "last_login")
  private OffsetDateTime lastLogin;

  @Column(name = "failed_login_attempts", nullable = false)
  private Integer failedLoginAttempts = 0;

  @Column(name = "account_locked_until")
  private OffsetDateTime accountLockedUntil;

  @Type(JsonBinaryType.class)
  @Column(name = "metadata", columnDefinition = "jsonb")
  private Map<String, Object> metadata;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
    if (enabled == null) {
      enabled = true;
    }
    if (failedLoginAttempts == null) {
      failedLoginAttempts = 0;
    }
  }

  // Constructors
  public User() {
  }

  public User(String name, String email, UserRole role) {
    this.name = name;
    this.email = email;
    this.role = role;
  }

  // Getters and Setters
  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public OffsetDateTime getLastLogin() {
    return lastLogin;
  }

  public void setLastLogin(OffsetDateTime lastLogin) {
    this.lastLogin = lastLogin;
  }

  public Integer getFailedLoginAttempts() {
    return failedLoginAttempts;
  }

  public void setFailedLoginAttempts(Integer failedLoginAttempts) {
    this.failedLoginAttempts = failedLoginAttempts;
  }

  public OffsetDateTime getAccountLockedUntil() {
    return accountLockedUntil;
  }

  public void setAccountLockedUntil(OffsetDateTime accountLockedUntil) {
    this.accountLockedUntil = accountLockedUntil;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Checks if account is currently locked.
   */
  public boolean isAccountLocked() {
    if (accountLockedUntil == null) {
      return false;
    }
    return OffsetDateTime.now().isBefore(accountLockedUntil);
  }

  /**
   * Records a successful login.
   */
  public void recordSuccessfulLogin() {
    this.lastLogin = OffsetDateTime.now();
    this.failedLoginAttempts = 0;
    this.accountLockedUntil = null;
  }

  /**
   * Records a failed login attempt.
   * Locks account after 5 failed attempts for 30 minutes.
   */
  public void recordFailedLogin() {
    this.failedLoginAttempts++;

    if (this.failedLoginAttempts >= 5) {
      this.accountLockedUntil = OffsetDateTime.now().plusMinutes(30);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof User))
      return false;
    User user = (User) o;
    return id != null && id.equals(user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
