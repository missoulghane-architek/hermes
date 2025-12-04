package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.model.User;
import com.m2it.hermes.domain.valueobject.Email;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(Email email);
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByResetPasswordToken(String token);
    List<User> findAll();
    void delete(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(Email email);
}
