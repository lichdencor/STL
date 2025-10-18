package io.stl.stl_core.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.stl.stl_core.model.entity.TransactionType;

/**
 * Repository for TransactionType (reference data).
 */
@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, UUID> {

  /**
   * Find transaction type by name.
   */
  Optional<TransactionType> findByName(String name);

  /**
   * Find all transaction types ordered by name.
   */
  List<TransactionType> findAllByOrderByNameAsc();
}
