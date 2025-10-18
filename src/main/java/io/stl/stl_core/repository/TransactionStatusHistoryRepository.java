package io.stl.stl_core.repository;

import io.stl.stl_core.model.enums.TransactionStatus;
import io.stl.stl_core.model.entity.TransactionStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TransactionStatusHistory (APPEND-ONLY).
 */
@Repository
public interface TransactionStatusHistoryRepository extends JpaRepository<TransactionStatusHistory, UUID> {

  /**
   * Get all status changes for a transaction, ordered by time.
   */
  @Query("SELECT h FROM TransactionStatusHistory h WHERE h.transaction.id = :transactionId ORDER BY h.updatedAt DESC")
  List<TransactionStatusHistory> findByTransactionId(@Param("transactionId") UUID transactionId);

  /**
   * Get the current (latest) status for a transaction.
   */
  @Query("SELECT h FROM TransactionStatusHistory h WHERE h.transaction.id = :transactionId ORDER BY h.updatedAt DESC LIMIT 1")
  Optional<TransactionStatusHistory> findLatestByTransactionId(@Param("transactionId") UUID transactionId);

  /**
   * Find all transactions with a specific current status.
   * Note: This is expensive - consider denormalizing current_status to
   * Transaction table.
   */
  @Query("""
      SELECT h FROM TransactionStatusHistory h
      WHERE h.status = :status
      AND h.updatedAt = (
          SELECT MAX(h2.updatedAt)
          FROM TransactionStatusHistory h2
          WHERE h2.transaction.id = h.transaction.id
      )
      """)
  List<TransactionStatusHistory> findByCurrentStatus(@Param("status") TransactionStatus status);
}
