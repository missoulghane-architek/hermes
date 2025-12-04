package com.m2it.hermes.application.port.in;
import com.m2it.hermes.application.dto.AuthenticationResponse;
import com.m2it.hermes.domain.model.User;
public interface AuthenticationUseCase {
    void register(RegisterUserCommand command);
    AuthenticationResponse login(LoginCommand command);
    AuthenticationResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
    User getCurrentUser(String username);
    void verifyEmail(String token);
    void forgotPassword(ForgotPasswordCommand command);
    void resetPassword(ResetPasswordCommand command);
}
