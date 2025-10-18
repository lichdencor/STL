package io.stl.stl_core.model.enums;

/**
 * Type of actor performing an action in the audit trail.
 * Distinguishes between human users, business entities, and automated system
 * actions.
 */
public enum ActorType {
  USER("Human user performing manual action"),
  ENTITY("Business entity performing action"),
  SYSTEM("Automated system action (scheduled jobs, triggers, etc.)");

  private final String description;

  ActorType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Check if this actor type represents a human-initiated action.
   */
  public boolean isHumanInitiated() {
    return this == USER;
  }

  /**
   * Check if this actor type represents an automated action.
   */
  public boolean isAutomated() {
    return this == SYSTEM;
  }

  /**
   * Check if this actor type requires an actor_id (USER and ENTITY do, SYSTEM
   * doesn't).
   */
  public boolean requiresActorId() {
    return this != SYSTEM;
  }

  /**
   * Get display name for UI/logs.
   */
  public String getDisplayName() {
    return switch (this) {
      case USER -> "User";
      case ENTITY -> "Business Entity";
      case SYSTEM -> "System (Automated)";
    };
  }
}
