package com.senpro.jafrabackend.exceptions;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message + " not found.");
    }
}
