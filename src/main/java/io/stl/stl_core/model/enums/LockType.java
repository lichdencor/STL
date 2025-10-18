package io.stl.stl_core.model.enums;

/**
 * Type of lock or hold placed on a transaction.
 * Locks prevent transactions from being processed until certain conditions are
 * met.
 */
public enum LockType {
  HOLD("Transaction on hold, awaiting review or additional information"),
  MANUAL_APPROVAL("Transaction requires explicit manual approval before processing"),
  FRAUD_REVIEW("Transaction flagged for fraud review"),
  COMPLIANCE_CHECK("Transaction under compliance verification"),
  INSUFFICIENT_FUNDS("Transaction held due to insufficient funds");

  private final String description;

  LockType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Check if this lock type requires human intervention to resolve.
   */
  public boolean requiresManualIntervention() {
    return this == MANUAL_APPROVAL || this == FRAUD_REVIEW || this == COMPLIANCE_CHECK;
  }

  /**
   * Check if this lock type can be automatically resolved.
   */
  public boolean canBeAutoResolved() {
    return this == INSUFFICIENT_FUNDS;
  }

  /**
   * Check if this lock type is related to security or compliance.
   */
  public boolean isSecurityRelated() {
    return this == FRAUD_REVIEW || this == COMPLIANCE_CHECK;
  }

  /**
   * Get the priority level for resolving this lock.
   * 
   * @return 1=LOW, 2=MEDIUM, 3=HIGH, 4=CRITICAL
   */
  public int getPriority() {
    return switch (this) {
      case HOLD -> 1;
      case INSUFFICIENT_FUNDS -> 2;
      case MANUAL_APPROVAL, COMPLIANCE_CHECK -> 3;
      case FRAUD_REVIEW -> 4;
    };
  }
}
