package io.stl.stl_core.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.stl.stl_core.model.entity.Transaction;
import io.stl.stl_core.model.entity.TransactionStatusHistory;
import io.stl.stl_core.model.enums.TransactionStatus;
import io.stl.stl_core.repository.TransactionStatusHistoryRepository;

/**
 * Service for managing transaction status changes.
 * Enforces append-only status history.
 */
@Service
public class StatusService {

  private final TransactionStatusHistoryRepository statusRepository;

  public StatusService(TransactionStatusHistoryRepository statusRepository) {
    this.statusRepository = statusRepository;
  }

  /**
   * Sets the initial status for a newly created transaction.
   */
  @Transactional
  public TransactionStatusHistory setInitialStatus(Transaction transaction, String reason) {
    return addStatusHistory(transaction, TransactionStatus.PENDING, reason);
  }

  /**
   * Changes transaction status (creates new history entry, doesn't modify
   * existing).
   */
  @Transactional
  public TransactionStatusHistory changeStatus(Transaction transaction,
      TransactionStatus newStatus,
      String reason) {

    // Validate status transition
    TransactionStatus currentStatus = getCurrentStatus(transaction);
    validateStatusTransition(currentStatus, newStatus);

    return addStatusHistory(transaction, newStatus, reason);
  }

  /**
   * Gets the current (latest) status of a transaction.
   */
  @Transactional(readOnly = true)
  public TransactionStatus getCurrentStatus(Transaction transaction) {
    return statusRepository.findLatestByTransactionId(transaction.getId())
        .map(TransactionStatusHistory::getStatus)
        .orElse(TransactionStatus.PENDING); // Default if no history exists
  }

  /**
   * Gets all status history for a transaction (ordered by time).
   */
  @Transactional(readOnly = true)
  public List<TransactionStatusHistory> getStatusHistory(Transaction transaction) {
    return statusRepository.findByTransactionId(transaction.getId());
  }

  /**
   * Validates that a status transition is allowed.
   * Business rule: Cannot change from final states.
   */
  private void validateStatusTransition(TransactionStatus currentStatus,
      TransactionStatus newStatus) {

    if (currentStatus.isFinalState()) {
      throw new IllegalStateException(
          "Cannot change status from " + currentStatus +
              " - transaction is in a final state");
    }

    // Additional transition rules can be added here
    // For example: PENDING can only go to ACTIVE or CANCELED
    // ACTIVE can go to APPROVED, FAILED, REFUND, etc.
  }

  /**
   * Creates a new status history entry (append-only).
   */
  private TransactionStatusHistory addStatusHistory(Transaction transaction,
      TransactionStatus status,
      String reason) {
    TransactionStatusHistory history = new TransactionStatusHistory(
        transaction,
        status,
        reason);

    return statusRepository.save(history);
  }
}
