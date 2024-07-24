package com.baas.backend.exception;

public class InvalidTransferParametersException extends RuntimeException {
  public InvalidTransferParametersException(String message) {
    super(message);
  }
}
