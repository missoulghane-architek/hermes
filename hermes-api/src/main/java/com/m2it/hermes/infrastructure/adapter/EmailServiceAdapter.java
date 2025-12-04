package com.m2it.hermes.infrastructure.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.m2it.hermes.domain.port.out.EmailService;
import com.m2it.hermes.domain.valueobject.Email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailServiceAdapter implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(Email to, String username, String verificationToken) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to.getValue());
        message.setSubject("Verify your email address");
        message.setText(String.format("""
                                      Hello %s,
                                      
                                      Please click the following link to verify your email address:
                                      
                                      %s
                                      
                                      This link will expire in 24 hours.
                                      
                                      Best regards,
                                      The Hermes Team""",
                username, verificationUrl
        ));

        try {
            mailSender.send(message);
            log.info("Verification email sent to: {}", to.getValue());
        } catch (MailException e) {
            log.error("Failed to send verification email to: {}", to.getValue(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(Email to, String username, String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to.getValue());
        message.setSubject("Reset your password");
        message.setText(String.format("""
                                      Hello %s,
                                      
                                      We received a request to reset your password. Click the link below:
                                      
                                      %s
                                      
                                      If you didn't request this, please ignore this email.
                                      
                                      Best regards,
                                      The Hermes Team""",
                username, resetUrl
        ));

        try {
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to.getValue());
        } catch (MailException e) {
            log.error("Failed to send password reset email to: {}", to.getValue(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    @Override
    public void sendWelcomeEmail(Email to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to.getValue());
        message.setSubject("Welcome to Hermes!");
        message.setText(String.format("""
                                      Hello %s,
                                      
                                      Your email has been successfully verified!
                                      
                                      Welcome to Hermes. We're excited to have you on board.
                                      
                                      Best regards,
                                      The Hermes Team""",
                username
        ));

        try {
            mailSender.send(message);
            log.info("Welcome email sent to: {}", to.getValue());
        } catch (MailException e) {
            log.error("Failed to send welcome email to: {}", to.getValue(), e);
        }
    }
}
