package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;

public class InactiveAccountException extends GeneralHttpException {
  public InactiveAccountException(String message) {
    super(new ErrorData(message, null));
  }
}
