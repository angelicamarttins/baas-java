package com.baas.backend.exception;

public class UnavailableExternalServiceException extends RuntimeException {
  public UnavailableExternalServiceException() {
    super();
  }

  public UnavailableExternalServiceException(String message) {
    super(message);
  }

  public UnavailableExternalServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnavailableExternalServiceException(Throwable cause) {
    super(cause);
  }
}
