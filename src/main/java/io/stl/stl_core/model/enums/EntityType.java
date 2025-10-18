package io.stl.stl_core.model.enums;

/**
 * Type of business entity that can participate in transactions.
 * Categorizes organizations by their business function.
 */
public enum EntityType {
  BANK("Banking institution or financial services provider"),
  MERCHANT("Merchant, retailer, or e-commerce business"),
  SERVICE("Service provider (utilities, subscriptions, etc.)"),
  GOVERNMENT("Government entity or public institution"),
  PAYMENT_PROCESSOR("Payment gateway or processing service"),
  EXCHANGE("Currency exchange or trading platform"),
  OTHER("Other entity type not categorized above");

  private final String description;

  EntityType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Check if this entity type represents a financial institution.
   */
  public boolean isFinancialInstitution() {
    return this == BANK || this == PAYMENT_PROCESSOR || this == EXCHANGE;
  }

  /**
   * Check if this entity type typically receives payments (merchants, services).
   */
  public boolean isPaymentReceiver() {
    return this == MERCHANT || this == SERVICE || this == GOVERNMENT;
  }

  /**
   * Check if this entity type requires enhanced regulatory compliance.
   */
  public boolean requiresEnhancedCompliance() {
    return this == BANK || this == PAYMENT_PROCESSOR || this == EXCHANGE || this == GOVERNMENT;
  }

  /**
   * Get the typical role this entity type plays in transactions.
   */
  public String getTypicalRole() {
    return switch (this) {
      case BANK -> "Fund holder and transferor";
      case MERCHANT -> "Goods/services provider";
      case SERVICE -> "Service provider";
      case GOVERNMENT -> "Tax/fee collector";
      case PAYMENT_PROCESSOR -> "Transaction facilitator";
      case EXCHANGE -> "Currency converter";
      case OTHER -> "Various roles";
    };
  }
}
