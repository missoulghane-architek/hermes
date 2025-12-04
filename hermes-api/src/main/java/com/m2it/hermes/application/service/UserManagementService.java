package com.m2it.hermes.application.service;

import com.m2it.hermes.application.port.in.UpdateUserCommand;
import com.m2it.hermes.application.port.in.UserManagementUseCase;
import com.m2it.hermes.domain.exception.UserNotFoundException;
import com.m2it.hermes.domain.model.Role;
import com.m2it.hermes.domain.model.User;
import com.m2it.hermes.domain.port.out.RoleRepository;
import com.m2it.hermes.domain.port.out.UserRepository;
import com.m2it.hermes.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional
    public User updateUser(UpdateUserCommand command) {
        User user = userRepository.findById(command.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + command.getId()));

        // Update fields if provided
        if (command.getUsername() != null) {
            // Check if username is already taken by another user
            userRepository.findByUsername(command.getUsername())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(user.getId())) {
                            throw new IllegalArgumentException("Username already exists");
                        }
                    });
        }

        if (command.getEmail() != null) {
            Email newEmail = new Email(command.getEmail());
            // Check if email is already taken by another user
            userRepository.findByEmail(newEmail)
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(user.getId())) {
                            throw new IllegalArgumentException("Email already exists");
                        }
                    });
        }

        // Create updated user (domain model is immutable via builder)
        User updatedUser = User.builder()
                .id(user.getId())
                .username(command.getUsername() != null ? command.getUsername() : user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(command.getEmail() != null ? new Email(command.getEmail()) : user.getEmail())
                .password(user.getPassword())
                .enabled(command.getEnabled() != null ? command.getEnabled() : user.isEnabled())
                .emailVerified(user.isEmailVerified())
                .emailVerificationToken(user.getEmailVerificationToken())
                .emailVerificationTokenExpiry(user.getEmailVerificationTokenExpiry())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.delete(id);
    }

    @Override
    @Transactional
    public User assignRoleToUser(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        user.addRole(role);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User removeRoleFromUser(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        user.removeRole(role);

        return userRepository.save(user);
    }
}
