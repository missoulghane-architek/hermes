package com.m2it.hermes.application.port.in;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;
@Getter
@Builder
public class UpdateUserCommand {
    private final UUID id;
    private final String username;
    private final String email;
    private final Boolean enabled;
}
