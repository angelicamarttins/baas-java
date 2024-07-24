package com.baas.backend.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
  public AccountNotFoundException(UUID accountId) {
    super("Accout not found. AccountId: {}" + accountId);
  }
}
