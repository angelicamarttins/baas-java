package com.baas.backend.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public class AccountDto {

  public record Response(
    @JsonProperty("id") UUID accountId,
    @JsonProperty("saldo") BigDecimal balance,
    @JsonProperty("ativo") Boolean activeAccount,
    @JsonProperty("limiteDiario") BigDecimal dailyLimit
  ) {
  }

}
