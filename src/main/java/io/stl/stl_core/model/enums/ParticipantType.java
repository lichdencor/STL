package io.stl.stl_core.model.enums;

/**
 * Defines the type of participant in a transaction.
 * Determines whether the participant is an individual user or a business
 * entity.
 */
public enum ParticipantType {
  USER("Individual user participating in the transaction"),
  ENTITY("Business entity participating in the transaction");

  private final String description;

  ParticipantType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Check if the participant is an individual user.
   */
  public boolean isUser() {
    return this == USER;
  }

  /**
   * Check if the participant is a business entity.
   */
  public boolean isEntity() {
    return this == ENTITY;
  }
}
