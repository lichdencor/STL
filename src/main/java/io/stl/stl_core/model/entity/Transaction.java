package io.stl.stl_core.model.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Core transaction entity - IMMUTABLE and APPEND-ONLY.
 * Once created, transactions cannot be modified.
 * Status changes are tracked via TransactionStatusHistory.
 */
@Entity
@Table(name = "transactions")
@Immutable
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "type_id", nullable = false)
  private TransactionType type;

  @NotNull
  @PositiveOrZero
  @Column(name = "amount", precision = 20, scale = 8, nullable = false)
  private BigDecimal amount;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "currency_code", nullable = false)
  private Currency currency;

  @Type(JsonBinaryType.class)
  @Column(name = "payload", columnDefinition = "jsonb")
  private Map<String, Object> payload;

  @Column(name = "previous_hash", length = 64)
  private String previousHash; // NULL for genesis transaction

  @Column(name = "signature", columnDefinition = "TEXT")
  private String signature; // HMAC or digital signature

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "immutable", nullable = false)
  private Boolean immutable = true;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
    if (immutable == null) {
      immutable = true;
    }
  }

  // Constructors
  public Transaction() {
  }

  public Transaction(TransactionType type, BigDecimal amount, Currency currency) {
    this.type = type;
    this.amount = amount;
    this.currency = currency;
  }

  // Getters only (immutable entity)
  public UUID getId() {
    return id;
  }

  public TransactionType getType() {
    return type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Currency getCurrency() {
    return currency;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public String getPreviousHash() {
    return previousHash;
  }

  public String getSignature() {
    return signature;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public Boolean getImmutable() {
    return immutable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Transaction))
      return false;
    Transaction that = (Transaction) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
