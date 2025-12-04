package com.m2it.hermes.infrastructure.web.dto;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
