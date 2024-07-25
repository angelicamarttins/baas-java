package com.baas.backend.httpclient;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.baas.backend.data.dto.AccountDto;
import com.baas.backend.httpclient.utils.HttpClientUtils;
import java.util.UUID;
import lombok.AllArgsConstructor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountClient {

  private final HttpClientUtils httpClientUtils;

  //  @CircuitBreaker(name = "externalService")
  //  @Retry(name = "externalService")
  //  @TimeLimiter(name = "externalService")
  public AccountDto.Response getAccount(UUID accountId) {
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

  //  @TimeLimiter(name = "externalService")
  //  @CircuitBreaker(name = "externalService")
  //  @Retry(name = "externalService")
  public void updateAccountBalance(String transferData) {
    new Request.Builder()
      .post(RequestBody.create(transferData, MediaType.get(APPLICATION_JSON_VALUE)))
      .url(
        httpClientUtils
          .getBaseUrl()
          .addPathSegment("contas/saldo")
          .toString()
      )
      .build();
  }
}
