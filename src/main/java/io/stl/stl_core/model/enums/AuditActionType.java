package io.stl.stl_core.model.enums;

/**
 * Audit action types for tracking transaction modifications.
 * Every action performed on a transaction must be logged with one of these
 * types.
 */
public enum AuditActionType {
  CREATE("Transaction created"),
  UPDATE_STATUS("Status updated"),
  REFUND("Refund issued"),
  CANCEL("Transaction canceled"),
  APPROVE("Transaction approved"),
  REJECT("Transaction rejected"),
  LOCK("Transaction locked"),
  UNLOCK("Transaction unlocked"),
  VERIFY_SIGNATURE("Signature verification performed"),
  VERIFY_CHAIN("Hash chain verification performed");

  private final String description;

  AuditActionType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Check if this action modifies the transaction state.
   */
  public boolean isStateModifying() {
    return this == UPDATE_STATUS || this == REFUND || this == CANCEL ||
        this == APPROVE || this == REJECT || this == LOCK || this == UNLOCK;
  }

  /**
   * Check if this action is related to security verification.
   */
  public boolean isSecurityAction() {
    return this == VERIFY_SIGNATURE || this == VERIFY_CHAIN;
  }

  /**
   * Check if this action represents transaction creation.
   */
  public boolean isCreationAction() {
    return this == CREATE;
  }

  /**
   * Get the severity level for monitoring/alerting.
   * 
   * @return 1=INFO, 2=WARNING, 3=CRITICAL
   */
  public int getSeverityLevel() {
    return switch (this) {
      case CREATE, APPROVE, UNLOCK, VERIFY_SIGNATURE, VERIFY_CHAIN -> 1;
      case UPDATE_STATUS, LOCK -> 2;
      case REFUND, CANCEL, REJECT -> 3;
    };
  }
}
