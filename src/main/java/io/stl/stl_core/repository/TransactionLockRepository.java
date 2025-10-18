package io.stl.stl_core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.stl.stl_core.model.entity.TransactionLock;

/**
 * Repository for TransactionLock (APPEND-ONLY).
 */
@Repository
public interface TransactionLockRepository extends JpaRepository<TransactionLock, UUID> {

  /**
   * Get all locks for a transaction.
   */
  List<TransactionLock> findByTransactionId(UUID transactionId);

  /**
   * Find active (non-expired) locks for a transaction.
   */
  @Query("""
      SELECT l FROM TransactionLock l
      WHERE l.transaction.id = :transactionId
      AND (l.expiresAt IS NULL OR l.expiresAt > CURRENT_TIMESTAMP)
      """)
  List<TransactionLock> findActiveLocksByTransactionId(@Param("transactionId") UUID transactionId);

  /**
   * Check if a transaction has any active locks.
   */
  @Query("""
      SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
      FROM TransactionLock l
      WHERE l.transaction.id = :transactionId
      AND (l.expiresAt IS NULL OR l.expiresAt > CURRENT_TIMESTAMP)
      """)
  boolean hasActiveLocks(@Param("transactionId") UUID transactionId);
}
