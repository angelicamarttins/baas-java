package com.baas.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public record Account(
  @JsonProperty("id") UUID accountId,
  @JsonProperty("saldo") BigDecimal balance,
  @JsonProperty("ativo") Boolean activeAccount,
  @JsonProperty("limiteDiario") BigDecimal dailyLimit
) {
}
