package io.stl.stl_core.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.stl.stl_core.model.entity.Participant;
import io.stl.stl_core.model.enums.ParticipantRole;

/**
 * Repository for Participant entities (APPEND-ONLY).
 */
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

  /**
   * Find all participants for a transaction.
   */
  List<Participant> findByTransactionId(UUID transactionId);

  /**
   * Find participants by role.
   */
  @Query("SELECT p FROM Participant p WHERE p.transaction.id = :transactionId AND p.role = :role")
  List<Participant> findByTransactionIdAndRole(@Param("transactionId") UUID transactionId,
      @Param("role") ParticipantRole role);

  /**
   * Find all transactions a participant is involved in.
   */
  @Query("SELECT p FROM Participant p WHERE p.participantId = :participantId ORDER BY p.createdAt DESC")
  List<Participant> findByParticipantId(@Param("participantId") UUID participantId);
}
