package com.baas.backend.exception;

public class InsufficientBalanceException extends RuntimeException {
  public InsufficientBalanceException() {
    super("Insufficient balance for transfer");
  }
}
