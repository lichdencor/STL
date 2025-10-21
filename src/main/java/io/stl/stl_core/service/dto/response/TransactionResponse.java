package io.stl.stl_core.service.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import io.stl.stl_core.model.enums.TransactionStatus;

/**
 * Response DTO for transaction data.
 * Contains only fields safe to expose to clients.
 * Does NOT include sensitive fields like signature, previousHash.
 */
public class TransactionResponse {

  private UUID id;
  private String typeName;
  private BigDecimal amount;
  private String currencyCode;
  private String currencySymbol;
  private TransactionStatus currentStatus;
  private Map<String, Object> payload;
  private OffsetDateTime createdAt;

  public TransactionResponse() {
  }

  public TransactionResponse(UUID id, String typeName, BigDecimal amount, String currencyCode,
      String currencySymbol, TransactionStatus currentStatus, OffsetDateTime createdAt) {
    this.id = id;
    this.typeName = typeName;
    this.amount = amount;
    this.currencyCode = currencyCode;
    this.currencySymbol = currencySymbol;
    this.currentStatus = currentStatus;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
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

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  public TransactionStatus getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(TransactionStatus currentStatus) {
    this.currentStatus = currentStatus;
  }

  public Map<String, Object> getPayload() {
    return payload;
  }

  public void setPayload(Map<String, Object> payload) {
    this.payload = payload;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

}
