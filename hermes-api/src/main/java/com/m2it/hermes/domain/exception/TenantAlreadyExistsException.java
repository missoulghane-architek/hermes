package com.m2it.hermes.domain.exception;

public class TenantAlreadyExistsException extends DomainException {
    public TenantAlreadyExistsException(String message) {
        super(message);
    }
}
