package io.stl.stl_core.security.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.stl.stl_core.model.entity.User;
import io.stl.stl_core.model.enums.UserRole;

/**
 * Spring security UserDetails implementation representing an authenticated
 * user.
 * Contains user information extracted from JWT token or loaded on database.
 */
public class UserPrincipal implements UserDetails {
  private final UUID id;
  private final String email;
  private final UserRole role;
  private final String password;
  private final boolean enabled;

  // Constructor for JWT token with no password
  public UserPrincipal(UUID id, String email, UserRole role) {
    this(id, email, role, null, true);
  }

  // Full constructor
  public UserPrincipal(UUID id, String email, UserRole role, String password, boolean enabled) {
    this.id = id;
    this.email = email;
    this.role = role;
    this.password = password;
    this.enabled = enabled;
  }

  /**
   * Creates a UserPrincipal from a User
   */
  public static UserPrincipal create(User user) {
    return new UserPrincipal(
        user.getId(),
        user.getEmail(),
        user.getRole(),
        user.getPasswordHash(),
        true);
  }

  // Getters
  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public UserRole getRole() {
    return role;
  }

  // UserDetails interface methods
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(() -> "ROLE_" + role.name());
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email; // Use email as username
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String toString() {
    return "UserPrincipal{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", role=" + role +
        ", enabled=" + enabled +
        '}';
  }
}
