package io.stl.stl_core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.stl.stl_core.model.entity.TransactionAudit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for TransactionAudit (APPEND-ONLY).
 */
@Repository
public interface TransactionAuditRepository extends JpaRepository<TransactionAudit, UUID> {

  /**
   * Get all audit records for a transaction.
   */
  @Query("SELECT a FROM TransactionAudit a WHERE a.transaction.id = :transactionId ORDER BY a.timestamp DESC")
  List<TransactionAudit> findByTransactionId(@Param("transactionId") UUID transactionId);

  /**
   * Get audit records by actor.
   */
  @Query("SELECT a FROM TransactionAudit a WHERE a.actorId = :actorId ORDER BY a.timestamp DESC")
  List<TransactionAudit> findByActorId(@Param("actorId") UUID actorId);

  /**
   * Find the most recent audit record (for hash chaining).
   */
  @Query("SELECT a FROM TransactionAudit a ORDER BY a.timestamp DESC LIMIT 1")
  Optional<TransactionAudit> findLatestAudit();
}
