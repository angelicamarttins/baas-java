package com.baas.backend.httpclient;

import com.baas.backend.data.dto.CustomerDto;
import com.baas.backend.httpclient.utils.HttpClientUtils;
import java.util.UUID;
import lombok.AllArgsConstructor;
import okhttp3.Request;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegisterClient {

  private final HttpClientUtils httpClientUtils;

  public CustomerDto.Response getCustomer(UUID customerId) {
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

}
