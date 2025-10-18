package io.stl.stl_core.repository;

import io.stl.stl_core.model.entity.Transaction;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for Transaction entities.
 * APPEND-ONLY: Only supports INSERT operations, no UPDATE or DELETE.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

  /**
   * Find the most recent transaction (for hash chaining).
   * 
   * @return Optional containing the latest transaction by creation time
   */
  @Query("SELECT t FROM Transaction t ORDER BY t.createdAt DESC LIMIT 1")
  Optional<Transaction> findLatestTransaction();

  /**
   * Find transactions within a date range.
   */
  @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end ORDER BY t.createdAt DESC")
  List<Transaction> findByCreatedAtBetween(@Param("start") OffsetDateTime start,
      @Param("end") OffsetDateTime end);

  /**
   * Find transactions by type.
   */
  @Query("SELECT t FROM Transaction t WHERE t.type.id = :typeId ORDER BY t.createdAt DESC")
  List<Transaction> findByTypeId(@Param("typeId") UUID typeId);

  /**
   * Find transactions by currency.
   */
  @Query("SELECT t FROM Transaction t WHERE t.currency.code = :currencyCode ORDER BY t.createdAt DESC")
  List<Transaction> findByCurrencyCode(@Param("currencyCode") String currencyCode);

  /**
   * Count transactions created after a specific date.
   */
  @Query("SELECT COUNT(t) FROM Transaction t WHERE t.createdAt > :since")
  long countCreatedSince(@Param("since") OffsetDateTime since);

  // Note: No update() or delete() methods - enforcing append-only semantics
  // The @Immutable annotation on the entity prevents accidental updates
}
