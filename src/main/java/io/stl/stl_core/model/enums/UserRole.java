package io.stl.stl_core.model.enums;

import java.util.Set;

/**
 * User roles for Role-Based Access Control (RBAC).
 * Defines what actions users can perform in the system.
 */
public enum UserRole {
  INGESTER("Can create and submit transactions", Set.of("CREATE_TRANSACTION", "VIEW_OWN_TRANSACTIONS")),
  READER("Can read transactions and reports", Set.of("VIEW_TRANSACTIONS", "VIEW_REPORTS")),
  AUDITOR("Can read audit logs and perform compliance reviews",
      Set.of("VIEW_TRANSACTIONS", "VIEW_AUDIT_LOGS", "VIEW_REPORTS", "EXPORT_DATA")),
  ADMIN("Full system access including user management", Set.of("*")); // "*" means all permissions

  private final String description;
  private final Set<String> permissions;

  UserRole(String description, Set<String> permissions) {
    this.description = description;
    this.permissions = permissions;
  }

  public String getDescription() {
    return description;
  }

  public Set<String> getPermissions() {
    return permissions;
  }

  /**
   * Check if this role has a specific permission.
   */
  public boolean hasPermission(String permission) {
    return permissions.contains("*") || permissions.contains(permission);
  }

  /**
   * Check if this role can create transactions.
   */
  public boolean canCreateTransactions() {
    return this == INGESTER || this == ADMIN;
  }

  /**
   * Check if this role can view transactions.
   */
  public boolean canViewTransactions() {
    return this != INGESTER || this == ADMIN;
  }

  /**
   * Check if this role can access audit logs.
   */
  public boolean canAccessAuditLogs() {
    return this == AUDITOR || this == ADMIN;
  }

  /**
   * Check if this role can modify system configuration.
   */
  public boolean canModifySystem() {
    return this == ADMIN;
  }

  /**
   * Check if this role has administrative privileges.
   */
  public boolean isAdmin() {
    return this == ADMIN;
  }

  /**
   * Get the security clearance level.
   * 
   * @return 1=LOW (INGESTER), 2=MEDIUM (READER), 3=HIGH (AUDITOR), 4=FULL (ADMIN)
   */
  public int getClearanceLevel() {
    return switch (this) {
      case INGESTER -> 1;
      case READER -> 2;
      case AUDITOR -> 3;
      case ADMIN -> 4;
    };
  }
}
