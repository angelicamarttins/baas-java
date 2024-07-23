package com.baas.backend.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public class TransferDto {

  public record Request(
    @JsonProperty("idCliente") UUID customerId,
    @JsonProperty("valor") BigDecimal value,
    @JsonProperty("conta") Accounts accounts
  ) {
  }

  public record Response(UUID transferId) {
  }

  public record Accounts(
    @JsonProperty("idOrigem") UUID sourceAccountId,
    @JsonProperty("idDestino") UUID targetAccountId
  ) {
  }

}
