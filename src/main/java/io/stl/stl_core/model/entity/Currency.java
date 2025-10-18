package io.stl.stl_core.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

/**
 * Represents a currency used in transactions.
 * Immutable reference data.
 */
@Entity
@Table(name = "currencies")
@Immutable
public class Currency {

  @Id
  @Column(name = "code", length = 3, nullable = false)
  private String code; // USD, EUR, etc.

  @NotBlank
  @Column(name = "name", nullable = false)
  private String name; // US Dollar, Euro

  @Column(name = "symbol", length = 3)
  private String symbol; // $, €

  @NotNull
  @Positive
  @Column(name = "precision", nullable = false)
  private Integer precision; // Number of decimal places (e.g., 2 for USD)

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  // Constructors
  public Currency() {
  }

  public Currency(String code, String name, String symbol, Integer precision) {
    this.code = code;
    this.name = name;
    this.symbol = symbol;
    this.precision = precision;
  }

  // Getters only (immutable)
  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getSymbol() {
    return symbol;
  }

  public Integer getPrecision() {
    return precision;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Currency))
      return false;
    Currency currency = (Currency) o;
    return code != null && code.equals(currency.code);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
