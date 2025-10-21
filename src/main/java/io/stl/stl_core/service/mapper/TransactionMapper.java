package io.stl.stl_core.service.mapper;

import org.springframework.stereotype.Component;

import io.stl.stl_core.model.entity.Transaction;
import io.stl.stl_core.model.enums.TransactionStatus;
import io.stl.stl_core.service.StatusService;
import io.stl.stl_core.service.dto.response.TransactionResponse;

/**
 * Mapper utility for converting between Transaction entities and DTOs.
 * Follows the DTO pattern to separate internal representation from API
 * responses.
 */
@Component
public class TransactionMapper {

  private final StatusService statusService;

  public TransactionMapper(StatusService statusService) {
    this.statusService = statusService;
  }

  /**
   * Converts a Transaction entity to a TransactionResponse DTO.
   * Only includes fields safe to expose via API.
   * 
   * SECURITY NOTE: Does NOT include:
   * - signature (internal integrity field)
   * - previousHash (internal chain field)
   * - Any other sensitive internal fields
   */
  public TransactionResponse toResponse(Transaction transaction) {
    if (transaction == null) {
      return null;
    }

    TransactionResponse response = new TransactionResponse();

    // Basic fields
    response.setId(transaction.getId());
    response.setAmount(transaction.getAmount());
    response.setCreatedAt(transaction.getCreatedAt());

    // Related entity fields (denormalized for API convenience)
    response.setTypeName(transaction.getType().getName());
    response.setCurrencyCode(transaction.getCurrency().getCode());
    response.setCurrencySymbol(transaction.getCurrency().getSymbol());

    // Current status (computed from status history)
    TransactionStatus currentStatus = statusService.getCurrentStatus(transaction);
    response.setCurrentStatus(currentStatus);

    // Optional payload
    response.setPayload(transaction.getPayload());

    return response;
  }

  /**
   * Converts a list of transactions to response DTOs.
   */
  public java.util.List<TransactionResponse> toResponseList(java.util.List<Transaction> transactions) {
    return transactions.stream()
        .map(this::toResponse)
        .toList();
  }
}
