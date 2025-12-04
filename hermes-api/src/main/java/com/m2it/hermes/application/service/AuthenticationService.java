package com.m2it.hermes.application.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.m2it.hermes.application.dto.AuthenticationResponse;
import com.m2it.hermes.application.port.in.AuthenticationUseCase;
import com.m2it.hermes.application.port.in.LoginCommand;
import com.m2it.hermes.application.port.in.RegisterUserCommand;
import com.m2it.hermes.domain.exception.InvalidTokenException;
import com.m2it.hermes.domain.exception.UserAlreadyExistsException;
import com.m2it.hermes.domain.exception.UserNotFoundException;
import com.m2it.hermes.domain.model.RefreshToken;
import com.m2it.hermes.domain.model.Role;
import com.m2it.hermes.domain.model.User;
import com.m2it.hermes.domain.port.out.PasswordEncoder;
import com.m2it.hermes.domain.port.out.RefreshTokenRepository;
import com.m2it.hermes.domain.port.out.RoleRepository;
import com.m2it.hermes.domain.port.out.UserRepository;
import com.m2it.hermes.domain.valueobject.Email;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final com.m2it.hermes.domain.port.out.EmailService emailService;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshTokenExpiration;

    @Value("${app.email.verification-token-expiration:86400000}")
    private long emailVerificationTokenExpiration;

    @Value("${app.email.reset-password-token-expiration:3600000}")
    private long resetPasswordTokenExpiration;

    @Override
    @Transactional
    public void register(RegisterUserCommand command) {

        Email email = new Email(command.getEmail());

        // Check if user already exists
        if (userRepository.existsByUsername(command.getUsername())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists");
        }

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusSeconds(emailVerificationTokenExpiration / 1000);

        System.out.println("***************** Register ********************");
        System.out.println("Generated Token: " + verificationToken);
        System.out.println("Token Expiry: " + tokenExpiry);

        // Create user
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(command.getUsername())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .email(email)
                .password(passwordEncoder.encode(command.getPassword()))
                .enabled(false)
                .emailVerified(false)
                .emailVerificationToken(verificationToken)
                .emailVerificationTokenExpiry(tokenExpiry)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Assign default USER role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.addRole(userRole);

        // Save user
        User savedUser = userRepository.save(user);

        System.out.println("Saved User Token: " + savedUser.getEmailVerificationToken());
        System.out.println("Saved User Token Expiry: " + savedUser.getEmailVerificationTokenExpiry());

        // Send verification email
        emailService.sendVerificationEmail(email, user.getUsername(), verificationToken);
    }

    @Override
    @Transactional
    public AuthenticationResponse login(LoginCommand command) {
        
        // Find user by email or username
        User user = command.getLogin().contains("@") ? userRepository.findByEmail(new Email(command.getLogin())).orElseThrow(() -> new UserNotFoundException("Invalid credentials"))
                        : userRepository.findByUsername(command.getLogin()).orElseThrow(() -> new UserNotFoundException("Invalid credentials"));
  
        // Validate password
        if (!passwordEncoder.matches(command.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        // Check if user is enabled
        if (!user.isEmailVerified()) {
            throw new IllegalStateException("Email is not verified");
        }

        // Check if user is enabled
        if (!user.isEnabled()) {
            throw new IllegalStateException("User account is disabled");
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Delete old refresh tokens and save new one
        refreshTokenRepository.deleteByUserId(user.getId());
        saveRefreshToken(user.getId(), refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(String refreshToken) {
        // Find refresh token
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        // Check if token is expired or revoked
        if (token.isExpired() || token.isRevoked()) {
            throw new InvalidTokenException("Refresh token is expired or revoked");
        }

        // Find user
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Generate new access token
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Revoke old refresh token and save new one
        refreshTokenRepository.deleteByUserId(user.getId());
        saveRefreshToken(user.getId(), newRefreshToken);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        refreshTokenRepository.deleteByUserId(token.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {

        // Find user by verification token
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification token"));

        // Check if token is expired
        if (user.getEmailVerificationTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getEmailVerificationTokenExpiry())) {
            throw new InvalidTokenException("Verification token has expired");
        }

        // Verify email
        user.verifyEmail();
        user.enable();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(com.m2it.hermes.application.port.in.ForgotPasswordCommand command) {
        Email email = new Email(command.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + command.getEmail()));

        // Generate reset password token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusSeconds(resetPasswordTokenExpiration / 1000);

        // Set reset token
        user.setResetPasswordToken(resetToken, tokenExpiry);
        userRepository.save(user);

        // Send reset password email
        emailService.sendPasswordResetEmail(email, user.getUsername(), resetToken);
    }

    @Override
    @Transactional
    public void resetPassword(com.m2it.hermes.application.port.in.ResetPasswordCommand command) {
        // Find user by reset password token
        User user = userRepository.findByResetPasswordToken(command.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset password token"));

        // Check if token is expired
        if (user.getResetPasswordTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getResetPasswordTokenExpiry())) {
            throw new InvalidTokenException("Reset password token has expired");
        }

        // Reset password
        user.resetPassword(passwordEncoder.encode(command.getNewPassword()));
        userRepository.save(user);
    }

    private void saveRefreshToken(UUID userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token(token)
                .userId(userId)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
