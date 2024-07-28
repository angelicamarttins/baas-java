package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;

public class TransferNotFoundException extends GeneralHttpException {
  public TransferNotFoundException(ErrorData errorData) {
    super(errorData);
  }
}
