package com.m2it.hermes.application.port.in;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class UpdateTenantCommand {
    private final UUID id;
    private final String lastName;
    private final String firstName;
    private final String email;
    private final String phone;
    private final LocalDate birthDate;
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;
}
