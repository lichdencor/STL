package io.stl.stl_core.model.enums;

/**
 * Defines the type of participant in a transaction.
 * Determines whether the participant is an individual user or a business
 * entity.
 */
public enum ParticipantRole {
  SENDER("Initiates the transaction"),
  RECEIVER("Receives the transaction"),
  APPROVER("Approves the transaction"),
  FEE("Fee collection entity"),
  TAX("Tax collection entity");

  private final String description;

  ParticipantRole(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Check if this role involves monetary transfer (debit)
   */
  public boolean isDebitRole() {
    return this == SENDER || this == FEE || this == TAX;
  }

  /**
   * Check if this role involves monetary receipt (credit)
   */
  public boolean isCreditRole() {
    return this == RECEIVER || this == FEE || this == TAX;
  }

  /**
   * Check if this role is for authorization only (no monetary transfer).
   */
  public boolean isAuthorizationRole() {
    return this == APPROVER;
  }

  /**
   * Check if this role is for authorization only (no monetary transfer).
   */
  public boolean isBusinessRole() {
    return this == FEE || this == TAX;
  }
}
