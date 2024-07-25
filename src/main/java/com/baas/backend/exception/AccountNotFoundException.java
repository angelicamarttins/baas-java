package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class AccountNotFoundException extends GeneralHttpException {
  public AccountNotFoundException(UUID accountId) {
    super(new ErrorData("Account not found. AccountId: " + accountId, HttpStatus.NOT_FOUND));
  }
}
