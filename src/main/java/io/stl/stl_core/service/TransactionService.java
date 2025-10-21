package io.stl.stl_core.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.stl.stl_core.model.entity.Currency;
import io.stl.stl_core.model.entity.Participant;
import io.stl.stl_core.model.entity.Transaction;
import io.stl.stl_core.model.entity.TransactionType;
import io.stl.stl_core.model.enums.ActorType;
import io.stl.stl_core.repository.CurrencyRepository;
import io.stl.stl_core.repository.ParticipantRepository;
import io.stl.stl_core.repository.TransactionRepository;
import io.stl.stl_core.repository.TransactionTypeRepository;
import io.stl.stl_core.service.dto.request.ParticipantRequest;
import io.stl.stl_core.service.dto.request.TransactionCreateRequest;
import io.stl.stl_core.service.dto.response.TransactionResponse;

/**
 * Main service for transaction operations.
 * Coordinates between repositories and enforces append-only semantics.
 * 
 * APPEND-ONLY ENFORCEMENT:
 * - Only provides CREATE operations
 * - No UPDATE or DELETE methods
 * - Status changes create new history entries
 */
@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final CurrencyRepository currencyRepository;
  private final TransactionTypeRepository transactionTypeRepository;
  private final ParticipantRepository participantRepository;
  private final TransactionValidationService validationService;
  private final AuditService auditService;
  private final StatusService statusService;

  public TransactionService(TransactionRepository transactionRepository,
      CurrencyRepository currencyRepository,
      TransactionTypeRepository transactionTypeRepository,
      ParticipantRepository participantRepository,
      TransactionValidationService validationService,
      AuditService auditService,
      StatusService statusService) {
    this.transactionRepository = transactionRepository;
    this.currencyRepository = currencyRepository;
    this.transactionTypeRepository = transactionTypeRepository;
    this.participantRepository = participantRepository;
    this.validationService = validationService;
    this.auditService = auditService;
    this.statusService = statusService;
  }

  /**
   * Creates a new transaction (COMMAND - modifies state).
   * Enforces append-only by only allowing creation, never updates.
   * 
   * @param request   Transaction creation request
   * @param actorId   ID of the user/entity creating the transaction
   * @param actorType Type of actor (USER, ENTITY, SYSTEM)
   * @return The created transaction
   */
  @Transactional
  public Transaction createTransaction(TransactionCreateRequest request,
      UUID actorId,
      ActorType actorType) {

    // 1. VALIDATE - Check business rules
    validationService.validateTransactionRequest(request);

    // 2. LOAD REFERENCES - Get currency and type
    Currency currency = currencyRepository.findById(request.getCurrencyCode())
        .orElseThrow(() -> new IllegalArgumentException("Currency not found"));

    TransactionType type = transactionTypeRepository.findById(request.getTypeId())
        .orElseThrow(() -> new IllegalArgumentException("Transaction type not found"));

    // 3. CREATE TRANSACTION - Build the entity
    Transaction transaction = new Transaction(type, request.getAmount(), currency);

    // Set optional payload if provided
    if (request.getPayload() != null) {
      // Note: Requires setter or builder on Transaction entity
      // For now, assuming entity supports this
    }

    // TODO Sprint 4: Add previousHash and signature
    // String previousHash = calculatePreviousHash();
    // String signature = signTransaction(transaction);

    // 4. SAVE TRANSACTION - Append-only insert
    Transaction savedTransaction = transactionRepository.save(transaction);

    // 5. CREATE AUDIT LOG - Record the creation
    auditService.logTransactionCreation(savedTransaction, actorId, actorType);

    // 6. SET INITIAL STATUS - Start as PENDING
    statusService.setInitialStatus(savedTransaction, "Transaction created");

    // 7. LINK PARTICIPANTS - Create participant relationships
    linkParticipants(savedTransaction, request.getParticipants());

    return savedTransaction;
  }

  /**
   * Gets a transaction by ID (QUERY - read-only).
   */
  @Transactional(readOnly = true)
  public Transaction getTransactionById(UUID id) {
    return transactionRepository.findById(id)
        .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + id));
  }

  /**
   * Lists all transactions (QUERY - read-only).
   * TODO: Add pagination in future iterations.
   */
  @Transactional(readOnly = true)
  public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  /**
   * Gets transactions by type (QUERY - read-only).
   */
  @Transactional(readOnly = true)
  public List<Transaction> getTransactionsByType(UUID typeId) {
    return transactionRepository.findByTypeId(typeId);
  }

  /**
   * Gets transactions by currency (QUERY - read-only).
   */
  @Transactional(readOnly = true)
  public List<Transaction> getTransactionsByCurrency(String currencyCode) {
    return transactionRepository.findByCurrencyCode(currencyCode);
  }

  /**
   * Converts a Transaction entity to a response DTO.
   * Hides sensitive fields (signature, previousHash).
   */
  public TransactionResponse toResponse(Transaction transaction) {
    TransactionResponse response = new TransactionResponse();
    response.setId(transaction.getId());
    response.setTypeName(transaction.getType().getName());
    response.setAmount(transaction.getAmount());
    response.setCurrencyCode(transaction.getCurrency().getCode());
    response.setCurrencySymbol(transaction.getCurrency().getSymbol());
    response.setCurrentStatus(statusService.getCurrentStatus(transaction));
    response.setPayload(transaction.getPayload());
    response.setCreatedAt(transaction.getCreatedAt());

    return response;
  }

  /**
   * Links participants to a transaction (append-only).
   * Creates new Participant entities for each participant in the request.
   */
  private void linkParticipants(Transaction transaction, List<ParticipantRequest> participantRequests) {
    for (ParticipantRequest participantRequest : participantRequests) {
      Participant participant = new Participant(
          transaction,
          participantRequest.getParticipantType(),
          participantRequest.getParticipantId(),
          participantRequest.getRole());

      // Set amount if provided
      if (participantRequest.getAmount() != null) {
        // Note: Requires setter or builder on Participant entity
        // For now, assuming entity supports this
      }

      participantRepository.save(participant);
    }
  }

  /**
   * Custom exception for transaction not found.
   */
  public static class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
      super(message);
    }
  }

  // ============================================================
  // NO UPDATE OR DELETE METHODS - ENFORCING APPEND-ONLY
  // ============================================================

  // ❌ public void updateTransaction(...) { } // NOT ALLOWED
  // ❌ public void deleteTransaction(...) { } // NOT ALLOWED

  // Status changes go through StatusService (creates new history entries)
  // Modifications are done by creating new transactions, not updating existing
  // ones
}
