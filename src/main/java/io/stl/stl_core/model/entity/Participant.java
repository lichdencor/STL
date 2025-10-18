package io.stl.stl_core.model.entity;

import io.stl.stl_core.model.enums.ParticipantRole;
import io.stl.stl_core.model.enums.ParticipantType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Links participants (Users or BusinessEntities) to transactions.
 * IMMUTABLE and APPEND-ONLY - relationships cannot be modified after creation.
 */
@Entity
@Table(name = "participants", uniqueConstraints = @UniqueConstraint(columnNames = { "transaction_id", "participant_id",
    "role" }))
@Immutable
public class Participant {

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
  @Column(name = "participant_type", nullable = false)
  private ParticipantType participantType;

  @NotNull
  @Column(name = "participant_id", nullable = false)
  private UUID participantId; // References User.id or BusinessEntity.id

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private ParticipantRole role;

  @PositiveOrZero
  @Column(name = "amount", precision = 20, scale = 8)
  private BigDecimal amount; // Optional: fractional share of transaction

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  // Constructors
  public Participant() {
  }

  public Participant(Transaction transaction, ParticipantType participantType,
      UUID participantId, ParticipantRole role) {
    this.transaction = transaction;
    this.participantType = participantType;
    this.participantId = participantId;
    this.role = role;
  }

  // Getters only (immutable)
  public UUID getId() {
    return id;
  }

  public Transaction getTransaction() {
    return transaction;
  }

  public ParticipantType getParticipantType() {
    return participantType;
  }

  public UUID getParticipantId() {
    return participantId;
  }

  public ParticipantRole getRole() {
    return role;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Participant))
      return false;
    Participant that = (Participant) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
