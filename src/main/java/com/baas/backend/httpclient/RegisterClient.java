package com.baas.backend.httpclient;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.httpclient.utils.HttpClientUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegisterClient {

  private final HttpClientUtils httpClientUtils;

  @TimeLimiter(name = "externalService")
  @CircuitBreaker(name = "register")
  @Retry(name = "externalService")
  public CompletableFuture<CustomerDto.Response> getCustomer(UUID customerId) {
    return CompletableFuture.supplyAsync(
      () -> {
        Request request = new Request.Builder()
          .get()
          .url(
            httpClientUtils
              .getBaseUrl()
              .addPathSegment("clientes")
              .addPathSegment(customerId.toString())
              .toString()
          )
          .build();

        return httpClientUtils.extractBody(request, CustomerDto.Response.class);
      }
    );
  }

}
