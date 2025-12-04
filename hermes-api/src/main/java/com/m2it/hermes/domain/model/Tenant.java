package com.m2it.hermes.domain.model;

import com.m2it.hermes.domain.valueobject.Address;
import com.m2it.hermes.domain.valueobject.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    private UUID id;
    private String lastName;
    private String firstName;
    private Email email;
    private String phone;
    private LocalDate birthDate;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
