package io.stl.stl_core.model.entity;

import io.stl.stl_core.model.enums.ActorType;
import io.stl.stl_core.model.enums.AuditActionType;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Append-only audit log for all transaction-related actions.
 * Records who did what, when, and provides tamper detection via hash chaining.
 */
@Entity
@Table(name = "transaction_audit", indexes = {
    @Index(name = "idx_audit_transaction", columnList = "transaction_id, timestamp DESC")
})
@Immutable
public class TransactionAudit {

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
  @Column(name = "actor_type", nullable = false)
  private ActorType actorType;

  @Column(name = "actor_id")
  private UUID actorId; // Nullable for SYSTEM actions

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "action_type", nullable = false)
  private AuditActionType actionType;

  @Type(JsonBinaryType.class)
  @Column(name = "metadata", columnDefinition = "jsonb")
  private Map<String, Object> metadata;

  @Column(name = "previous_hash", length = 64)
  private String previousHash; // For tamper detection

  @Column(name = "signature", columnDefinition = "TEXT")
  private String signature;

  @Column(name = "timestamp", nullable = false, updatable = false)
  private OffsetDateTime timestamp;

  @PrePersist
  protected void onCreate() {
    if (timestamp == null) {
      timestamp = OffsetDateTime.now();
    }
  }

  // Constructors
  public TransactionAudit() {
  }

  public TransactionAudit(Transaction transaction, ActorType actorType, UUID actorId,
      AuditActionType actionType) {
    this.transaction = transaction;
    this.actorType = actorType;
    this.actorId = actorId;
    this.actionType = actionType;
  }

  // Getters only (immutable)
  public UUID getId() {
    return id;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public ActorType getActorType() {
    return actorType;
  }

  public UUID getActorId() {
    return actorId;
  }

  public AuditActionType getActionType() {
    return actionType;
  }

  public Map<String, Object> getMetadata() {
    return metadata;
  }

  public String getPreviousHash() {
    return previousHash;
  }

  public String getSignature() {
    return signature;
  }

  public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof TransactionAudit))
      return false;
    TransactionAudit that = (TransactionAudit) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
