package com.m2it.hermes.application.port.in;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterUserCommand {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private final String password;
}
