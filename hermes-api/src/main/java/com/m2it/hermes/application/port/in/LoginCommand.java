package com.m2it.hermes.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCommand {
    private final String login;
    private final String password;
}
