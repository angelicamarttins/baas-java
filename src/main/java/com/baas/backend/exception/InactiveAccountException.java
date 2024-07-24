package com.baas.backend.exception;

public class InactiveAccountException extends RuntimeException {
  public InactiveAccountException(String message) {
    super(message);
  }
}
