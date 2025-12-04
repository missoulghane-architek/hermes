package com.m2it.hermes.application.port.in;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateTenantCommand {
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
