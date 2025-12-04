package com.m2it.hermes.domain.exception;

public class LeaseAlreadyExistsException extends DomainException {
    public LeaseAlreadyExistsException(String message) {
        super(message);
    }
}
