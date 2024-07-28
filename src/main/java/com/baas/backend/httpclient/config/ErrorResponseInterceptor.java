package com.baas.backend.httpclient.config;

import com.baas.backend.exception.ClientException;
import com.baas.backend.exception.TooManyRequestClientException;
import com.baas.backend.exception.common.ErrorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class ErrorResponseInterceptor implements Interceptor {

  private final ObjectMapper objectMapper;

  @Override
  public Response intercept(Chain chain) {
    Request request = chain.request();
    try {
      Response response = chain.proceed(request);
      int statusCode = response.code();

      if (isErrorStatusCode(statusCode)) {
        assert response.body() != null;
        String responseBody = response.body().string();

        log.error(
          "Error while requesting data during HTTP request [{} - {}] - Status Code: {} - Response Body: {}",
          request.method(), request.url(), statusCode, responseBody
        );

        String errorReason = new StringBuilder()
          .append("Response Body: ")
          .append(objectMapper.readTree(responseBody))
          .append(". ")
          .append(request.method())
          .append(" in URL ")
          .append(request.url())
          .toString();


        response.close();

        ErrorData errorData = new ErrorData(errorReason, HttpStatus.SERVICE_UNAVAILABLE);
        if (isErrorTooManyRequest(statusCode)) {
          throw new TooManyRequestClientException(errorData, HttpStatus.valueOf(statusCode));
        }

        throw new ClientException(errorData, HttpStatus.valueOf(statusCode));
      }
      return response;
    } catch (IOException e) {
      log.error(
        "Error while attempting to make a HTTP request [{} - {}] - Error: {}",
        request.method(), request.url(), e.getMessage(), e
      );

      String errorReason = new StringBuilder()
        .append(request.method())
        .append(" in URL ")
        .append(request.url())
        .toString();

      ErrorData errorData = new ErrorData(errorReason, HttpStatus.SERVICE_UNAVAILABLE);

      throw new ClientException(errorData, e);
    }
  }

  private boolean isErrorStatusCode(int statusCode) {
    return HttpStatus.valueOf(statusCode).isError();
  }

  private boolean isErrorTooManyRequest(int statusCode) {
    return HttpStatus.valueOf(statusCode).isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS);
  }
}
