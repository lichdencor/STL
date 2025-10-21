package io.stl.stl_core.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.stl.stl_core.model.entity.Transaction;
import io.stl.stl_core.model.entity.TransactionAudit;
import io.stl.stl_core.model.enums.ActorType;
import io.stl.stl_core.model.enums.AuditActionType;
import io.stl.stl_core.repository.TransactionAuditRepository;

/**
 * Service for managing transaction audit logs.
 * All transaction actions must be logged through this service.
 */
@Service
public class AuditService {
  public final TransactionAuditRepository auditRepository;

  public AuditService(TransactionAuditRepository auditRepository) {
    this.auditRepository = auditRepository;
  }

  /**
   * Logs a transaction action perfomed by user.
   */
  @Transactional
  public TransactionAudit logUserAction(Transaction transaction, UUID userId, AuditActionType actionType,
      Map<String, Object> metaData) {
    return createAuditEntry(transaction, ActorType.USER, userId, actionType, metaData);
  }

  /**
   * Logs a transaction action perfomed by an entity
   */
  @Transactional
  public TransactionAudit logEntityAction(Transaction transaction, UUID entityId, AuditActionType actionType,
      Map<String, Object> metaData) {
    return createAuditEntry(transaction, ActorType.ENTITY, entityId, actionType, metaData);
  }

  /**
   * Logs a transaction action perfomed by the system
   */
  @Transactional
  public TransactionAudit logSystemAction(Transaction transaction, AuditActionType actionType,
      Map<String, Object> metaData) {
    return createAuditEntry(transaction, ActorType.SYSTEM, null, actionType, metaData);
  }

  /**
   * Logs transaction creation with automatic metadata.
   */
  public TransactionAudit logTransactionCreation(Transaction transaction, UUID actorId, ActorType actorType) {
    Map<String, Object> metadata = Map.of(
        "amount", transaction.getAmount().toString(),
        "currency", transaction.getCurrency().getCode(),
        "type", transaction.getType().getName());

    return createAuditEntry(transaction, actorType, actorId, AuditActionType.CREATE, metadata);
  }

  /**
   * Create an audit entry with hash chaining support.
   * In Spring 4, this will add previousHash and signature
   */
  private TransactionAudit createAuditEntry(Transaction transaction,
      ActorType actorType,
      UUID actorId,
      AuditActionType actionType,
      Map<String, Object> metadata) {

    // TODO: sprint 4: Add hash chaining logic here
    // String previousHash = getLatestAuditHash(Transaction);

    // TODO: sprint 4: Generate digital signature
    // String signature = signatureService.sign(auditData);

    TransactionAudit audit = new TransactionAudit(
        transaction,
        actorType,
        actorId,
        actionType);

    // Set metadata if provided
    if (metadata != null && !metadata.isEmpty()) {
      // note: This requires a setter or builder pattern on TransactionAudit
      // For now, assuming the entity has a way to set metadata
    }

    return auditRepository.save(audit);
  }

  /**
   * Gets the latest audit hash for a transaction.
   * Will be implemented in Sprint 4.
   */
  private String getLatestAuditHash(Transaction transaction) {
    return auditRepository.findLatestAudit().map(audit -> {
      // TODO sprint 4: Calculate hash of audit entry
      return "hash_placeholder";
    }).orElse(null);
  }
}
