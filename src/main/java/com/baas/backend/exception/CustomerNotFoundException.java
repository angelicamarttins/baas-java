package com.baas.backend.exception;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends GeneralHttpException {
  public CustomerNotFoundException(UUID customerId) {
    super(new ErrorData("Customer not found. CustomerId: " + customerId, HttpStatus.NOT_FOUND));
  }
}
