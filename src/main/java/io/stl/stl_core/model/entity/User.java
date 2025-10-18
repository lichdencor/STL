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

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, Object> metadata) {
    this.metadata = metadata;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
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
