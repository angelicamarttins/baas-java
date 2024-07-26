package com.baas.backend.httpclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkHttpClientConfig {

  @Bean
  public OkHttpClient okHttpClient(ObjectMapper objectMapper) {
    return new OkHttpClient.Builder()
      .addInterceptor(new ErrorResponseInterceptor(objectMapper))
      .build();
  }
}
