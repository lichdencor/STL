package io.stl.stl_core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.stl.stl_core.model.entity.Currency;

/**
 * Repository for Currency (reference data, rarely changes).
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

  /**
   * Find currency by code (primary key).
   * 
   * @param code ISO 4217 currency code (e.g., "USD")
   */
  Optional<Currency> findByCode(String code);

  /**
   * Find all active currencies ordered by code.
   */
  List<Currency> findAllByOrderByCodeAsc();
}
