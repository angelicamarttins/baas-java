package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;
import org.springframework.http.HttpStatus;

public class TooManyRequestClientException extends GeneralHttpException {
  public TooManyRequestClientException(ErrorData errorData, HttpStatus httpStatus) {
    super(errorData, httpStatus, null);
  }

  public TooManyRequestClientException(ErrorData errorData, Throwable throwable) {
    super(errorData, HttpStatus.BAD_GATEWAY, throwable);
  }

  public TooManyRequestClientException(ErrorData errorData, HttpStatus httpStatus, Throwable throwable) {
    super(errorData, httpStatus, throwable);
  }
}
