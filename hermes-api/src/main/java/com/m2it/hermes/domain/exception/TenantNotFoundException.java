package com.m2it.hermes.domain.exception;

public class TenantNotFoundException extends DomainException {
    public TenantNotFoundException(String message) {
        super(message);
    }
}
