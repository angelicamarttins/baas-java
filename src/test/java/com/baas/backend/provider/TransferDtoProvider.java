package com.baas.backend.provider;

import com.baas.backend.data.dto.TransferDto;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class TransferDtoProvider {

  public static TransferDto.Request createTransferRequest(BigDecimal value) {
    return new TransferDto.Request(
      UUID.randomUUID(),
      Objects.requireNonNullElse(value, BigDecimal.TEN),
      createTransferAccounts()
    );
  }

  public static TransferDto.TransferAccounts createTransferAccounts() {
    return new TransferDto.TransferAccounts(
      UUID.randomUUID(),
      UUID.randomUUID()
    );
  }

}
