package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;
import org.springframework.http.HttpStatus;

public class UnavailableExternalServiceException extends GeneralHttpException {
  public UnavailableExternalServiceException(String message, HttpStatus httpStatus) {
    super(new ErrorData(message, httpStatus));
  }

  public UnavailableExternalServiceException(String message, HttpStatus httpStatus, Throwable cause) {
    super(new ErrorData(message, httpStatus), cause);
  }
}
