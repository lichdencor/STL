package io.stl.stl_core.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.stl.stl_core.model.entity.BusinessEntity;
import io.stl.stl_core.model.enums.EntityType;

/**
 * Repository for BusinessEntity.
 */
@Repository
public interface BusinessEntityRepository extends JpaRepository<BusinessEntity, UUID> {

  /**
   * Find entities by type.
   */
  List<BusinessEntity> findByType(EntityType type);

  /**
   * Find entity by name (assuming names should be unique per type).
   */
  Optional<BusinessEntity> findByNameAndType(String name, EntityType type);

  /**
   * Find all entities of a specific type, ordered by name.
   */
  List<BusinessEntity> findByTypeOrderByNameAsc(EntityType type);
}
