package io.stl.stl_core.service.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import io.stl.stl_core.model.enums.ParticipantRole;
import io.stl.stl_core.model.enums.ParticipantType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Request DTO for participant information in a transaction.
 */
public class ParticipantRequest {

  @NotNull(message = "Participant type is required")
  private ParticipantType participantType;

  @NotNull(message = "Participant ID is required")
  private UUID participantId;

  @NotNull(message = "Participant role is required")
  private ParticipantRole role;

  @PositiveOrZero(message = "Amount must be zero or positive")
  private BigDecimal amount;

  // constructors
  public ParticipantRequest() {
  }

  public ParticipantRequest(ParticipantType participantType, UUID participantId, ParticipantRole role) {
    this.participantType = participantType;
    this.participantId = participantId;
    this.role = role;
  }

  public ParticipantRequest(ParticipantType participantType, UUID participantId, ParticipantRole role,
      BigDecimal amount) {
    this.participantType = participantType;
    this.participantId = participantId;
    this.role = role;
    this.amount = amount;
  }

  public ParticipantType getParticipantType() {
    return participantType;
  }

  public void setParticipantType(ParticipantType participantType) {
    this.participantType = participantType;
  }

  public UUID getParticipantId() {
    return participantId;
  }

  public void setParticipantId(UUID participantId) {
    this.participantId = participantId;
  }

  public ParticipantRole getRole() {
    return role;
  }

  public void setRole(ParticipantRole role) {
    this.role = role;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  @Override
  public String toString() {
    return "ParticipantRequest{" +
        "participantType=" + participantType +
        ", participantId=" + participantId +
        ", role=" + role +
        ", amount=" + amount +
        '}';
  }
}
