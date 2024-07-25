package com.baas.backend.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GeneralHttpException extends RuntimeException {

  private final ErrorData errorData;
  private final HttpStatus httpStatus;

  public GeneralHttpException(ErrorData errorData, HttpStatus httpStatus, Throwable cause) {
    super(errorData.errorReason(), cause);

    this.errorData = errorData;
    this.httpStatus = httpStatus != null ? httpStatus : HttpStatus.BAD_REQUEST;
  }

  public GeneralHttpException(ErrorData errorData) {
    this(errorData, HttpStatus.BAD_REQUEST, null);
  }

  public GeneralHttpException(ErrorData errorData, Throwable cause) {
    this(errorData, HttpStatus.BAD_REQUEST, cause);
  }

}
