package io.stl.stl_core.model.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.Immutable;

import io.stl.stl_core.model.enums.LockType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents holds or locks on transactions requiring manual approval or
 * review.
 * IMMUTABLE and APPEND-ONLY.
 */
@Entity
@Table(name = "transaction_locks")
@Immutable
public class TransactionLock {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "transaction_id", nullable = false)
  private Transaction transaction;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "lock_type", nullable = false)
  private LockType lockType;

  @Column(name = "locked_by")
  private UUID lockedBy; // User or Entity ID

  @Column(name = "reason", columnDefinition = "TEXT")
  private String reason;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "expires_at")
  private OffsetDateTime expiresAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  // Constructors
  public TransactionLock() {
  }

  public TransactionLock(Transaction transaction, LockType lockType, UUID lockedBy, String reason) {
    this.transaction = transaction;
    this.lockType = lockType;
    this.lockedBy = lockedBy;
    this.reason = reason;
  }

  // Getters only (immutable)
  public UUID getId() {
    return id;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public LockType getLockType() {
    return lockType;
  }

  public UUID getLockedBy() {
    return lockedBy;
  }

  public String getReason() {
    return reason;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getExpiresAt() {
    return expiresAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TransactionLock))
      return false;
    TransactionLock that = (TransactionLock) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
