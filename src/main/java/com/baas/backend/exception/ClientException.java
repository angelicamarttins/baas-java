package com.baas.backend.exception;

import org.springframework.http.HttpStatus;

public class ClientException extends RuntimeException {
  public ClientException() {
    super();
  }

  public ClientException(String message) {
    super(message);
  }

  public ClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public ClientException(Throwable cause) {
    super(cause);
  }
}
