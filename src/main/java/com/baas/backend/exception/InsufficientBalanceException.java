package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;

public class InsufficientBalanceException extends GeneralHttpException {
  public InsufficientBalanceException() {
    super(new ErrorData("Saldo insuficiente para transferência", null));
  }
}
