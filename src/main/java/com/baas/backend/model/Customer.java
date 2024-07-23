package com.baas.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Customer(
  @JsonProperty("id") UUID customerId,
  @JsonProperty("nome") String customerName,
  @JsonProperty("telefone") String phone,
  @JsonProperty("tipoPessoa") AccountType accountType
) {
}
