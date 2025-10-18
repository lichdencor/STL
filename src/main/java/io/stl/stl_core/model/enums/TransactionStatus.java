package io.stl.stl_core.model.enums;

/**
 * Represents the lifecycle status of a transaction.
 * Used in TransactionStatusHistory for append-only status tracking.
 */
public enum TransactionStatus {
  PENDING("Transaction created, awaiting processing"),
  ACTIVE("Transaction is active and valid"),
  ON_HOLD("Transaction temporarily held for review"),
  APPROVED("Transaction approved by required authorities"),
  CANCELED("Transaction canceled before completion"),
  FAILED("Transaction failed during processing"),
  REFUND("Transaction refunded"),
  PARTIAL("Partial transaction completion");

  private final String description;

  TransactionStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Check if this status represents a final state (no further changes expected).
   */
  public boolean isFinalState() {
    return this == APPROVED || this == CANCELED || this == FAILED || this == REFUND;
  }

  /**
   * Check if this status allows transitions to other states.
   */
  public boolean isTransitionable() {
    return !isFinalState();
  }
}
