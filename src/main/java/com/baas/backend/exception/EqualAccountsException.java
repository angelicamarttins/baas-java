package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;

public class EqualAccountsException extends GeneralHttpException {
  public EqualAccountsException(ErrorData errorData) {
    super(errorData);
  }
}
