package com.baas.backend.advice;

import com.baas.backend.exception.common.ErrorData;
import com.baas.backend.exception.common.GeneralHttpException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErrorData>> handleValidationException(MethodArgumentNotValidException ex) {
    List<ErrorData> invalidArguments = new ArrayList<>();

    ex.getBindingResult()
      .getAllErrors()
      .forEach(error -> {
        System.out.println("ERROR = " + error);
        ErrorData errorData = new ErrorData(
          error.getDefaultMessage(),
          HttpStatus.BAD_REQUEST
        );

        invalidArguments.add(errorData);
      });

    ErrorData errorData = new ErrorData("Invalid argument", HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(
      invalidArguments,
      HttpStatus.BAD_REQUEST
    );
  }

}
