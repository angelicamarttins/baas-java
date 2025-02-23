package com.baas.backend.data.dto;

import com.baas.backend.model.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class CustomerDto {

  public record Response(
    @JsonProperty("id") UUID customerId,
    @JsonProperty("nome") String customerName,
    @JsonProperty("telefone") String phone,
    @JsonProperty("tipoPessoa") AccountType accountType
  ) {
  }

}
