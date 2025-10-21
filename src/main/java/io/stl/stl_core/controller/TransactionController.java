package io.stl.stl_core.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.stl.stl_core.model.entity.Transaction;
import io.stl.stl_core.model.entity.TransactionAudit;
import io.stl.stl_core.model.entity.TransactionStatusHistory;
import io.stl.stl_core.model.enums.ActorType;
import io.stl.stl_core.repository.TransactionAuditRepository;
import io.stl.stl_core.service.StatusService;
import io.stl.stl_core.service.TransactionService;
import io.stl.stl_core.service.dto.request.TransactionCreateRequest;
import io.stl.stl_core.service.dto.response.ApiResponse;
import io.stl.stl_core.service.dto.response.TransactionResponse;
import io.stl.stl_core.service.mapper.TransactionMapper;
import jakarta.validation.Valid;

/**
 * REST Controller for transaction operations.
 * 
 * APPEND-ONLY ENFORCEMENT:
 * - Only POST endpoints (creates new resources)
 * - NO PUT or PATCH endpoints (no updates)
 * - NO DELETE endpoints (no deletions)
 * 
 * All business logic is delegated to TransactionService.
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

  private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

  private final TransactionService transactionService;
  private final TransactionMapper transactionMapper;
  private final StatusService statusService;
  private final TransactionAuditRepository auditRepository;

  public TransactionController(TransactionService transactionService,
      TransactionMapper transactionMapper,
      StatusService statusService,
      TransactionAuditRepository auditRepository) {
    this.transactionService = transactionService;
    this.transactionMapper = transactionMapper;
    this.statusService = statusService;
    this.auditRepository = auditRepository;
  }

  /**
   * Creates a new transaction.
   * 
   * @param request Transaction creation request with validation
   * @return Created transaction with 201 status
   */
  @PostMapping
  public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
      @Valid @RequestBody TransactionCreateRequest request) {

    log.info("Creating transaction: type={}, amount={}, currency={}",
        request.getTypeId(), request.getAmount(), request.getCurrencyCode());

    // TODO Sprint 3: Get actual user from SecurityContext
    // For now, using system actor
    UUID actorId = null; // Will be from JWT in Sprint 3
    ActorType actorType = ActorType.SYSTEM;

    // Delegate to service
    Transaction transaction = transactionService.createTransaction(request, actorId, actorType);

    // Convert to DTO
    TransactionResponse response = transactionMapper.toResponse(transaction);

    log.info("Transaction created successfully: id={}", transaction.getId());

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(response));
  }

  /**
   * Retrieves a transaction by ID.
   * 
   * @param id Transaction UUID
   * @return Transaction details
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<TransactionResponse>> getTransaction(
      @PathVariable UUID id) {

    log.debug("Retrieving transaction: id={}", id);

    Transaction transaction = transactionService.getTransactionById(id);
    TransactionResponse response = transactionMapper.toResponse(transaction);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Lists all transactions.
   * 
   * TODO: Add pagination, filtering, and sorting in future iterations.
   * 
   * @return List of all transactions
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions() {

    log.debug("Retrieving all transactions");

    List<Transaction> transactions = transactionService.getAllTransactions();
    List<TransactionResponse> responses = transactionMapper.toResponseList(transactions);

    return ResponseEntity.ok(ApiResponse.success(responses));
  }

  /**
   * Gets transactions by type.
   * 
   * @param typeId Transaction type UUID
   * @return List of transactions of the specified type
   */
  @GetMapping("/by-type/{typeId}")
  public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByType(
      @PathVariable UUID typeId) {

    log.debug("Retrieving transactions by type: typeId={}", typeId);

    List<Transaction> transactions = transactionService.getTransactionsByType(typeId);
    List<TransactionResponse> responses = transactionMapper.toResponseList(transactions);

    return ResponseEntity.ok(ApiResponse.success(responses));
  }

  /**
   * Gets transactions by currency.
   * 
   * @param currencyCode ISO 4217 currency code (e.g., USD, EUR)
   * @return List of transactions in the specified currency
   */
  @GetMapping("/by-currency/{currencyCode}")
  public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByCurrency(
      @PathVariable String currencyCode) {

    log.debug("Retrieving transactions by currency: code={}", currencyCode);

    List<Transaction> transactions = transactionService.getTransactionsByCurrency(currencyCode);
    List<TransactionResponse> responses = transactionMapper.toResponseList(transactions);

    return ResponseEntity.ok(ApiResponse.success(responses));
  }

  /**
   * Gets the status history for a transaction.
   * Shows all status changes in chronological order.
   * 
   * @param id Transaction UUID
   * @return List of status changes
   */
  @GetMapping("/{id}/status-history")
  public ResponseEntity<ApiResponse<List<TransactionStatusHistory>>> getStatusHistory(
      @PathVariable UUID id) {

    log.debug("Retrieving status history for transaction: id={}", id);

    Transaction transaction = transactionService.getTransactionById(id);
    List<TransactionStatusHistory> history = statusService.getStatusHistory(transaction);

    return ResponseEntity.ok(ApiResponse.success(history));
  }

  /**
   * Gets the audit trail for a transaction.
   * Shows all actions performed on the transaction.
   * 
   * @param id Transaction UUID
   * @return List of audit entries
   */
  @GetMapping("/{id}/audit")
  public ResponseEntity<ApiResponse<List<TransactionAudit>>> getAuditTrail(
      @PathVariable UUID id) {

    log.debug("Retrieving audit trail for transaction: id={}", id);

    // Verify transaction exists
    transactionService.getTransactionById(id);

    List<TransactionAudit> audits = auditRepository.findByTransactionId(id);

    return ResponseEntity.ok(ApiResponse.success(audits));
  }

  // ============================================================
  // NO UPDATE OR DELETE ENDPOINTS - ENFORCING APPEND-ONLY
  // ============================================================

  // ❌ @PutMapping("/{id}") // NOT ALLOWED - No updates
  // ❌ @PatchMapping("/{id}") // NOT ALLOWED - No partial updates
  // ❌ @DeleteMapping("/{id}") // NOT ALLOWED - No deletions

  // Status changes should be done via dedicated endpoints that create new history
  // entries
  // Example: POST /api/v1/transactions/{id}/status (creates new status history)
}
