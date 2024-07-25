package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;
import org.springframework.http.HttpStatus;

public class ClientException extends GeneralHttpException {
  public ClientException(ErrorData errorData, HttpStatus httpStatus) {
    super(errorData, httpStatus, null);
  }

  public ClientException(ErrorData errorData, Throwable throwable) {
    super(errorData, HttpStatus.BAD_GATEWAY, throwable);
  }

  public ClientException(ErrorData errorData, HttpStatus httpStatus, Throwable throwable) {
    super(errorData, httpStatus, throwable);
  }
}
