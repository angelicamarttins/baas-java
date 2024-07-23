package com.baas.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Accounts(
  @JsonProperty("idOrigem") UUID sourceAccountId,
  @JsonProperty("idDestino") UUID targetAccountId
) {
}
