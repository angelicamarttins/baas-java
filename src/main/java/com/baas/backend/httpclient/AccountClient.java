package com.baas.backend.httpclient;

import com.baas.backend.httpclient.utils.HttpClientUtils;
import com.baas.backend.model.Account;
import java.util.UUID;
import lombok.AllArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountClient {

  private final HttpClientUtils httpClientUtils;

  public Account getAccount(UUID accountId) {
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

    return httpClientUtils.extractBody(request, Account.class);
  }
}
