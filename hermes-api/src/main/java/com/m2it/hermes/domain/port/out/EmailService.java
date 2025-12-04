package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.valueobject.Email;

public interface EmailService {
    void sendVerificationEmail(Email to, String username, String verificationToken);
    void sendPasswordResetEmail(Email to, String username, String resetToken);
    void sendWelcomeEmail(Email to, String username);
}
