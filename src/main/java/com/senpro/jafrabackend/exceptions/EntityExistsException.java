package com.senpro.jafrabackend.exceptions;

public class EntityExistsException extends Exception {
  public EntityExistsException(String message) {
    super(message + " already exists.");
  }
}
