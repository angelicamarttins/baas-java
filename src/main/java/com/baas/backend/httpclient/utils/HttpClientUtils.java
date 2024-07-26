package com.baas.backend.httpclient.utils;

import com.baas.backend.data.dto.TransferDataDto;
import com.baas.backend.exception.ClientException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpClientUtils {

  private final OkHttpClient client;
  private final ObjectMapper objectMapper;
  @Value("${external-services.url}")
  private String baseUrl;

  public HttpClientUtils(OkHttpClient client, ObjectMapper objectMapper) {
    this.client = client;
    this.objectMapper = objectMapper;
  }

  public HttpUrl.Builder getBaseUrl() {
    return Objects.requireNonNull(HttpUrl.parse(baseUrl)).newBuilder();
  }

  @SneakyThrows
  public <T> T extractBody(Request request, Class<T> clazz) {
    try (Response response = client.newCall(request).execute()) {
      String responseBody = Objects.requireNonNull(response.body()).string();
      log.info("code: {}, message: {}", response.code(), response.message());

      return objectMapper.readValue(responseBody, clazz);
    } catch (IOException exception) {
      log.error(
        "Error while extracting body data during HTTP request [{} - {}] - Error Message: {}",
        request.method(), request.url(), exception.getMessage(), exception
      );
      throw exception;
    }
  }

  @SneakyThrows
  public TransferDataDto.Response executeNewCall(Request request) {
    try (Response response = client.newCall(request).execute()) {
      return new TransferDataDto.Response(
        response.body().string(),
        response.code()
      );
    } catch (ClientException exception) {
      log.error(
        "Error while a HTTP request [{} - {}] - Error Message: {}",
        request.method(), request.url(), exception.getMessage(), exception
      );
      throw exception;
    }
  }

}
