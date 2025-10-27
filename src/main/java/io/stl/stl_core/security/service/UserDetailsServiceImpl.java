package io.stl.stl_core.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.stl.stl_core.model.entity.User;
import io.stl.stl_core.repository.UserRepository;
import io.stl.stl_core.security.dto.UserPrincipal;

/**
 * Spring Security UserDetailsService implementation.
 * Loads user details from database for authentication.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Loads user by email (username in Spring Security terms).
   * 
   * @param email User email
   * @return UserDetails object
   * @throws UsernameNotFoundException if user not found
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    log.debug("Loading user by email: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.warn("User not found: {}", email);
          return new UsernameNotFoundException("User not found with email: " + email);
        });

    log.debug("User loaded successfully: {}", email);
    return UserPrincipal.create(user);
  }
}
