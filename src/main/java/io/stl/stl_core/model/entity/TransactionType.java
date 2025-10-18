package io.stl.stl_core.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Defines types of transactions (PAYMENT, REFUND, TRANSFER, etc.).
 * Immutable reference data.
 */
@Entity
@Table(name = "transaction_types")
@Immutable
public class TransactionType {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotBlank
  @Column(name = "name", nullable = false, unique = true)
  private String name; // PAYMENT, REFUND, TRANSFER, etc.

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  // Constructors
  public TransactionType() {
  }

  public TransactionType(String name, String description) {
    this.name = name;
    this.description = description;
  }

  // Getters only (immutable)
  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TransactionType))
      return false;
    TransactionType that = (TransactionType) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
