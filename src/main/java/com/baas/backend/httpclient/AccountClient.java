package com.baas.backend.httpclient;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.httpclient.utils.HttpClientUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountClient {

  private final HttpClientUtils httpClientUtils;

  @TimeLimiter(name = "externalService")
  @CircuitBreaker(name = "accounts")
  @Retry(name = "externalService")
  public CompletableFuture<AccountDto.Response> getAccount(UUID accountId) {
    return CompletableFuture.supplyAsync(
      () -> {
        Request request = new Request.Builder()
          .get()
          .url(
            httpClientUtils
              .getBaseUrl()
              .addPathSegment("contas")
              .addPathSegment(accountId.toString())
              .toString()
          )
          .build();

        return httpClientUtils.extractBody(request, AccountDto.Response.class);
      }
    );
  }

  @TimeLimiter(name = "externalService")
  @CircuitBreaker(name = "accounts")
  @Retry(name = "externalService")
  public CompletableFuture<TransferDataDto.Response> updateAccountBalance(String transferData) {
    return CompletableFuture.supplyAsync(() -> {
      Request request = new Request.Builder()
        .put(RequestBody.create(transferData, MediaType.get(APPLICATION_JSON_VALUE)))
        .url(
          httpClientUtils
            .getBaseUrl()
            .addPathSegment("contas")
            .addPathSegment("saldos")
            .toString()
        )
        .build();

      return httpClientUtils.executeNewCall(request);
    });
  }
}
