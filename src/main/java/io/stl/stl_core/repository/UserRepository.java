package io.stl.stl_core.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.stl.stl_core.model.entity.User;
import io.stl.stl_core.model.enums.UserRole;

/**
 * Repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * Find user by email (unique constraint).
   */
  Optional<User> findByEmail(String email);

  /**
   * Find users by role.
   */
  List<User> findByRole(UserRole role);

  /**
   * Check if email already exists.
   */
  boolean existsByEmail(String email);
}
