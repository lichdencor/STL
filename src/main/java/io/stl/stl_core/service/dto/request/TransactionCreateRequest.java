package io.stl.stl_core.service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new transaction.
 * Contains validation rules for client input.
 */
public class TransactionCreateRequest {

  @NotNull(message = "Transaction type ID is required")
  private UUID typeId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
  @Digits(integer = 12, fraction = 8, message = "Amount has too many digits")
  private BigDecimal amount;

  @NotBlank(message = "Currency code is required")
  @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
  @Pattern(regexp = "[A-Z]{3}", message = "Currency code must be uppercase letters only")
  private String currencyCode;

  @NotEmpty(message = "Transaction must have at least one participant")
  @Valid
  private List<ParticipantRequest> participants;

  // We add an optional metada field to allow clients to attach custom data to the
  // transaction.
  private Map<String, Object> payload;

  // Constructor. With Fields and without
  public TransactionCreateRequest() {
  }

  public TransactionCreateRequest(UUID typeId, BigDecimal amount, String currencyCode,
      List<ParticipantRequest> participants, Map<String, Object> payload) {
    this.typeId = typeId;
    this.amount = amount;
    this.currencyCode = currencyCode;
    this.participants = participants;
  }

  public UUID getTypeId() {
    return typeId;
  }

  public void setTypeId(UUID typeId) {
    this.typeId = typeId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public List<ParticipantRequest> getParticipants() {
    return participants;
  }

  public void setParticipants(List<ParticipantRequest> participants) {
    this.participants = participants;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  @Override
  public String toString() {
    return "TransactionCreateRequest{" +
        "typeId=" + typeId +
        ", amount=" + amount +
        ", currencyCode='" + currencyCode + '\'' +
        ", participants=" + participants +
        ", payload=" + payload +
        '}';
  }
}
