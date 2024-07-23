package com.baas.backend.data.dto;

import com.baas.backend.model.Accounts;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequestDto(
  @JsonProperty("idCliente") UUID clientId,
  @JsonProperty("valor") BigDecimal value,
  @JsonProperty("conta") Accounts accounts
) {
}
