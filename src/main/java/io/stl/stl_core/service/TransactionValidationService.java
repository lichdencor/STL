package io.stl.stl_core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.stl.stl_core.model.enums.ParticipantRole;
import io.stl.stl_core.repository.CurrencyRepository;
import io.stl.stl_core.repository.TransactionTypeRepository;
import io.stl.stl_core.service.dto.request.ParticipantRequest;
import io.stl.stl_core.service.dto.request.TransactionCreateRequest;

/**
 * Service for validating transcation bussiness rules.
 * Enforces domain logic beyond simple data validation.
 */
@Service
public class TransactionValidationService {

  private final CurrencyRepository currencyRepository;
  private final TransactionTypeRepository transactionTypeRepository;

  public TransactionValidationService(CurrencyRepository currencyRepository,
      TransactionTypeRepository transactionTypeRepository) {
    this.currencyRepository = currencyRepository;
    this.transactionTypeRepository = transactionTypeRepository;
  }

  /**
   * Validate a transaction creation request.
   * Throws TransactionValidationException if any business rule is violated.
   */
  public void validateTransactionRequest(TransactionCreateRequest request) {
    validateCurrency(request.getCurrencyCode());
    validateTransactionType(request.getTypeId());
  }

  /**
   * Validates that currency exists.
   */
  public void validateCurrency(String currencyCode) {
    if (!currencyRepository.existsById(currencyCode)) {
      throw new TransactionValidationException("INVALID_CURRENCY",
          "Currency code does not exist: " + currencyCode);
    }
  }

  /**
   * Validates that transaction type exists.
   */
  private void validateTransactionType(UUID typeId) {
    if (!transactionTypeRepository.existsById(typeId)) {
      throw new TransactionValidationException("INVALID_TRANSACTION_TYPE",
          "Transaction type ID does not exist: " + typeId);
    }
  }

  /**
   * Validates participant business rules.
   */
  public void validateParticipants(List<ParticipantRequest> participants, BigDecimal transactionAmount) {

    // Rule 1: Must have at least one SENDER
    boolean hasSender = participants.stream().anyMatch(p -> p.getRole() == ParticipantRole.SENDER);

    if (!hasSender) {
      throw new TransactionValidationException("MISSING_SENDER",
          "Transaction must have at least one SENDER participant.");
    }

    // Rule 2: Must have at least one RECEIVER
    boolean hasReceiver = participants.stream().anyMatch(p -> p.getRole() == ParticipantRole.RECEIVER);

    if (!hasReceiver) {
      throw new TransactionValidationException("MISSING_RECEIVER",
          "Transaction must have at least one RECEIVER participant.");
    }

    // Rule 3: If participants have amounts, they must sum to transaction amount
    boolean allHaveAmounts = participants.stream().allMatch(p -> p.getAmount() != null);

    if (allHaveAmounts) {
      BigDecimal totalParticipantAmount = participants.stream()
          .map(ParticipantRequest::getAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      if (totalParticipantAmount.compareTo(transactionAmount) != 0) {
        throw new TransactionValidationException("AMOUNT_MISMATCH",
            "Sum of participant amounts (" + totalParticipantAmount +
                ") does not equal transaction amount (" + transactionAmount + ").");
      }
    }

    // Rule 4: Cannot have participant + role that conflict
    long uniqueParticipantRoles = participants.stream()
        .map(p -> p.getParticipantId().toString() + ":" + p.getRole().name())
        .distinct()
        .count();

    if (uniqueParticipantRoles != participants.size()) {
      throw new TransactionValidationException("DUPLICATE_PARTICIPANT_ROLE",
          "A participant cannot have conflicting roles in the same transaction.");
    }
  }

  /**
   * Custom validation exception for transaction validation errors.
   */
  public static class TransactionValidationException extends RuntimeException {
    private final String errorCode;

    public TransactionValidationException(String errorCode, String message) {
      super(message);
      this.errorCode = errorCode;
    }

    public String getErrorCode() {
      return errorCode;
    }
  }
}
