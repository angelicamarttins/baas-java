package com.baas.backend.httpclient;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.baas.backend.httpclient.utils.HttpClientUtils;
import lombok.AllArgsConstructor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BacenClient {

  private final HttpClientUtils httpClientUtils;

  //  @TimeLimiter(name = "externalService")
  //  @CircuitBreaker(name = "externalService")
  //  @Retry(name = "externalService")
  public void notifyBacenSuccessfulTransfer(String transferData) {
    new Request.Builder()
      .post(RequestBody.create(transferData, MediaType.get(APPLICATION_JSON_VALUE)))
      .url(
        httpClientUtils
          .getBaseUrl()
          .addPathSegment("notificacoes")
          .toString()
      )
      .build();
  }

}
