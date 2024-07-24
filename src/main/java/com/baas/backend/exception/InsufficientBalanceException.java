package com.baas.backend.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
  public InsufficientBalanceException(BigDecimal expectedValue, BigDecimal balance) {
    super("Insufficient balance for transfer. ExpectedValue: " + expectedValue + ", Balance: " + balance);
  }
}
