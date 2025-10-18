package io.stl.stl_core.model.entity;

import io.stl.stl_core.model.enums.TransactionStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Tracks status changes for transactions in an append-only fashion.
 * Each status change creates a new record - never updates existing ones.
 */
@Entity
@Table(name = "transaction_status_history", indexes = {
    @Index(name = "idx_status_history_transaction", columnList = "transaction_id, updated_at DESC")
})
@Immutable
public class TransactionStatusHistory {

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
  @Column(name = "status", nullable = false)
  private TransactionStatus status;

  @Column(name = "reason", columnDefinition = "TEXT")
  private String reason;

  @Column(name = "updated_at", nullable = false, updatable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    if (updatedAt == null) {
      updatedAt = OffsetDateTime.now();
    }
  }

  // Constructors
  public TransactionStatusHistory() {
  }

  public TransactionStatusHistory(Transaction transaction, TransactionStatus status, String reason) {
    this.transaction = transaction;
    this.status = status;
    this.reason = reason;
  }

  // Getters only (immutable)
  public UUID getId() {
    return id;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public String getReason() {
    return reason;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TransactionStatusHistory))
      return false;
    TransactionStatusHistory that = (TransactionStatusHistory) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
