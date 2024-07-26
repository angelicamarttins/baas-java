package com.baas.backend.advice;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

  @ExceptionHandler(GeneralHttpException.class)
  @ResponseBody
  public ResponseEntity<ErrorData> handleDominusExceptions(GeneralHttpException ex) {
    return new ResponseEntity<>(
      ex.getErrorData(),
      ex.getHttpStatus()
    );
  }

}
