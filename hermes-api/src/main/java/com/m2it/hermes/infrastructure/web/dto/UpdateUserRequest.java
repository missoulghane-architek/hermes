package com.m2it.hermes.infrastructure.web.dto;
import lombok.Data;
@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private Boolean enabled;
}
